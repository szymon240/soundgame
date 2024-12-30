package pl.soundgame.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import pl.soundgame.engine.Game
import java.io.FileNotFoundException

/**
 * Loads texture image from app assets to memory as Bitmap object
 *
 * @param textureName file path in assets/textures/
 * @param context App context for getting access to app assets
 * @return Bitmap object loaded from assets if textureName is present, missing_texture.png otherwise
 */
fun loadTextureBitmap(textureName: String, context: Context): Bitmap {
    return try {
        BitmapFactory.decodeStream(context.assets.open("textures/$textureName"))
    } catch (e: FileNotFoundException) {
        Log.e("Loading texture ERROR","Error, texture name not found: FileNotFoundException ${e.message}")
        BitmapFactory.decodeStream(context.assets.open("textures/missing_texture.png"))
    } catch (e: Exception) {
        Log.e("Loading texture ERROR","Error, texture couldn't be loaded:  ${e.message}")
        BitmapFactory.decodeStream(context.assets.open("textures/missing_texture.png"))
    }
}

/**
 * Class that extends OpenGL Renderer to be used for rendering 2D graphics in 3D space for the game
 *
 * @constructor
 * Create new instance of rednerer to be used in GLSurface
 *
 * @param context App main activity context
 * @param game Game object which GameObjects will be rendered and handel
 *
 * @author Adam Czyżak
 */
class GameGLRenderer(context: Context, game: Game) : GLSurfaceView.Renderer {
    private var context: Context
    private val TAG = "Renderer"
    private var game: Game

    private val pendingGLTasks = mutableListOf<() -> Unit>()
    var isContextReady = false
        private set

    init {
        Log.d(TAG, "Initializing renderer")
        this.context = context
        this.game = game
    }

    fun setContext(context: Context) {
        this.context = context
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        Log.d(TAG, "Creating surface...")
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // OpenGL initialization code
        game.mShaderProgram = ShaderProgram("foreground_vertex_shader.glsl", "foreground_fragment_shader.glsl", context)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        game.onCrateSurface()
        isContextReady = true
        Log.d(TAG, "Surface created!")

        synchronized(pendingGLTasks) {
            pendingGLTasks.forEach { it.invoke() }
            pendingGLTasks.clear()
        }
    }

    override fun onDrawFrame(unused: GL10) {
        game.onDrawFrame()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        Log.d(TAG, "Surface changed!")
        GLES20.glViewport(0, 0, width, height)
        game.onSurfaceChanged(width, height)
    }

    fun runWhenReady(task: () -> Unit) {
        if (isContextReady) {
            task.invoke()
        } else {
            synchronized(pendingGLTasks) {
                pendingGLTasks.add(task)
            }
        }
    }

    fun clickHandling(x: Float, y: Float) {
        runWhenReady {
            game.clickHandle(x, y)
        }
    }
}
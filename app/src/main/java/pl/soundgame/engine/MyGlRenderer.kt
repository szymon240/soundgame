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

fun loadTextureBitmap(textureName: String, context: Context): Bitmap {
    return BitmapFactory.decodeStream(context.assets.open("textures/$textureName"))
}


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
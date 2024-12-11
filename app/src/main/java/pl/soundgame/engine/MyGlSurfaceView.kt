package pl.soundgame.engine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import pl.soundgame.engine.Game

private const val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f
class GameGLSurfaceView(context: Context, game: Game) : GLSurfaceView(context) {
    private val TAG = "SurfaceView"
    private val renderer: GameGLRenderer
    private var previousX: Float = 0f
    private var previousY: Float = 0f
    init {
        Log.d(TAG, "Creating surface...")
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        renderer = GameGLRenderer(context, game)
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        Log.d(TAG, "Surface Created!")
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_UP -> {
                // Guarantees that eventual changes in scene caused by click action will be
                // processed in OpenGL thread and there won't be created a new one for this
                // task. This allows texture changing
                this.queueEvent {
                    renderer.clickHandling(x, y)
                }
            }
        }

        previousX = x
        previousY = y
        return true
    }
}
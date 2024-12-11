package pl.soundgame

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.soundgame.engine.GameGLSurfaceView

/**
 * The main entry point of the app, responsible for initializing the game and rendering the OpenGL view.
 *
 * @author Adam Czyżak & Szymon Szymankiewicz
 */
class MainActivity : AppCompatActivity() {
    private lateinit var gLView: GLSurfaceView

    /**
     * Called when the activity is first created.
     * Initializes the game and sets up the OpenGL surface view for rendering.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains its previous state.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val game = SoundGame(this)

        gLView = GameGLSurfaceView(this, game)
        setContentView(gLView)
    }
}
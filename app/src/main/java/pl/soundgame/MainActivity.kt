package pl.soundgame

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.soundgame.engine.GameGLSurfaceView
import pl.soundgame.engine.Scene
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.engine.shapes.createTextTexture

class MainActivity : AppCompatActivity() {
    private lateinit var gLView: GLSurfaceView
    private lateinit var soundPlayer: SoundPlayer

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val game = SoundGame(this)

        gLView = GameGLSurfaceView(this, game)
        setContentView(gLView)
    }
}
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
    private lateinit var soundPlayer1: SoundPlayer
    private lateinit var soundPlayer2: SoundPlayer
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundPlayer1 = SoundPlayer(this)
        soundPlayer1.initialize(R.raw.test)

        soundPlayer2 = SoundPlayer(this)
        soundPlayer2.initialize(R.raw.test)

        val scene = Scene()
        val game = SoundGame(scene)
        var zmienna = 1
        scene.setInitScene {
            var ob1 = GameObject( loadTextureBitmap("button.png", this), id="1")
            ob1.setOriginPosition(y=0.2f, x=0.5f)
            ob1.scale(0.25f) // scaling must be after changing position for desired effect
            var ob2  = GameObject(loadTextureBitmap("button.png", this), id="2")
            ob2.setOriginPosition(y=0.2f, x=-0.5f)
            ob2.scale(0.25f)
            var ob3 = GameObject( loadTextureBitmap("button.png", this), id="3")
            ob3.setOriginPosition( x=0.5f,y=-0.2f)
            ob3.scale(0.25f)
            var ob4  = GameObject(loadTextureBitmap("button.png", this), id="4")
            ob4.setOriginPosition(y=-0.2f, x=-0.5f)
            ob4.scale(0.25f)

            ob2.setClickAction {
                if (zmienna == 1){
                    ob2.logObjectInfo()
                    ob2.swapSprite(createTextTexture("hej- po kliknięciu",size=60f, background = loadTextureBitmap("button.png", this)))
                    zmienna = 2
                }
                else {
                    ob2.logObjectInfo()
                    ob2.swapSprite(createTextTexture("hej- po kliknięciu parzystym",size=60f, background = loadTextureBitmap("button.png", this)))
                    zmienna = 1
                }

            }

            ob1.setClickAction {
                soundPlayer1.setSound(R.raw.test)
                soundPlayer1.play()
            }

            ob4.setClickAction {
                if (!soundPlayer2.isPlaying()) {
                    soundPlayer2.setSound(R.raw.sound2)
                    soundPlayer2.play()
                } else {
                    soundPlayer2.pause()
                }
            }


            scene.addGameObject(ob1, ob2, ob3, ob4)
            var question  = GameObject(createTextTexture( game.someQuestion,size=60f, background = loadTextureBitmap("button.png", this)), id="5")
            question.setOriginPosition(y=0.55f)
            question.scale(0.3f)
            scene.addGameObject(question)
        }



        game.setAfterCreateSurface {

        }

        gLView = GameGLSurfaceView(this, game)
        setContentView(gLView)
    }
}
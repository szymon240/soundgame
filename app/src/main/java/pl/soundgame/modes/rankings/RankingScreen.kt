package pl.soundgame.modes.rankings

import android.content.Context
import pl.soundgame.R
import pl.soundgame.connection.CommunicationManager
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.engine.shapes.createTextTexture
import pl.soundgame.modes.GameMode
import pl.soundgame.modes.GameModeName

class RankingScreen(var context: Context, private val commManager: CommunicationManager, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    override fun returnGameModeScene(): Scene {

        val scene = Scene()

        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            val topText = TextBox(initialText = context.getString(R.string.achievements_screen),  width = 512, size = 32f)
            topText.setOriginPosition(y = 0.7f, x = 0f)
            topText.scale(0.6f)
            val tops10text = TextBox(initialText = context.getString(R.string.top_tens),  width = 512, size = 32f)
            tops10text.setOriginPosition(y = 0.05f, x = 0f)
            tops10text.scale(0.6f)
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
            scene.addGameObject( exitButton)
            val achievementsButton = Button(loadTextureBitmap("achievements2.png", context) )
            achievementsButton.setOriginPosition(y = 0.4f, x = -0.0f)
            achievementsButton.scale(0.4f)
            achievementsButton.onClickAction { changeModeCallback(GameModeName.ACHIEVEMENTS_SCREEN) }
            scene.addGameObject( achievementsButton)
            val top10Rhythm = Button(loadTextureBitmap("rhythmMode2.png", context) )
            top10Rhythm.setOriginPosition(y = -0.2f, x = -0.6f)
            top10Rhythm.scale(0.3f)
            top10Rhythm.onClickAction { changeModeCallback(GameModeName.TOP10_RHTHM) }
            scene.addGameObject( top10Rhythm)
            val top10Instrumental = Button(loadTextureBitmap("instrumentalMode.png", context) )
            top10Instrumental.setOriginPosition(y = -0.2f, x = -0.0f)
            top10Instrumental.scale(0.3f)
            top10Instrumental.onClickAction { changeModeCallback(GameModeName.TOP10_INSTRUMENTAL) }
            scene.addGameObject( top10Instrumental, topText, tops10text)

            val top10Pitch = Button(loadTextureBitmap("pitchMode.png",context) )
            top10Pitch.setOriginPosition(y = -0.2f, x = 0.6f)
            top10Pitch.scale(0.3f)
            top10Pitch.onClickAction { changeModeCallback(GameModeName.TOP10_INSTRUMENTAL) }
            scene.addGameObject( top10Instrumental, topText, tops10text, top10Pitch)
        }

        return scene
    }

}
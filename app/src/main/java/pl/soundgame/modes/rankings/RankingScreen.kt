package pl.soundgame.modes.rankings

import android.content.Context
import pl.soundgame.R
import pl.soundgame.connection.CommunicationManager
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
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
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
            scene.addGameObject( exitButton)

            val achievementsButton = Button(createTextTexture(context.getString(R.string.achievements_screen)) )
            achievementsButton.setOriginPosition(y = 0.4f, x = -0.0f)
            achievementsButton.scale(0.5f)
            achievementsButton.onClickAction { changeModeCallback(GameModeName.ACHIEVEMENTS_SCREEN) }
            scene.addGameObject( achievementsButton)

            val top10Rhythm = Button(createTextTexture(context.getString(R.string.top_10_rhythm)) )
            top10Rhythm.setOriginPosition(y = 0.0f, x = -0.0f)
            top10Rhythm.scale(0.5f)
            top10Rhythm.onClickAction { changeModeCallback(GameModeName.TOP10_RHTHM) }
            scene.addGameObject( top10Rhythm)

            val top10Instrumental = Button(createTextTexture(context.getString(R.string.top_10_instrumental)) )
            top10Instrumental.setOriginPosition(y = -0.4f, x = -0.0f)
            top10Instrumental.scale(0.5f)
            top10Instrumental.onClickAction { changeModeCallback(GameModeName.TOP10_INSTRUMENTAL) }
            scene.addGameObject( top10Instrumental)
        }

        return scene
    }

}
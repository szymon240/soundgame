package pl.soundgame.modes

import android.content.Context
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.loadTextureBitmap

class Settings(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {

    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }
        scene.setInitScene {
            val exitButton = Button(loadTextureBitmap("button.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.2f, x = -0.5f)
            exitButton.scale(0.25f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
            scene.addGameObject(exitButton)
        }

        return scene
    }
}
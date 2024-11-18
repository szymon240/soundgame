package pl.soundgame.modes

import android.content.Context
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.loadTextureBitmap

class Menu(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            val rhythmModeButton =
                Button(loadTextureBitmap("button.png", context), id = "rhythmModeButton")
            rhythmModeButton.setOriginPosition(
                y = 0.3f,
                x = 0f
            )  // Position button in the upper center
            rhythmModeButton.scale(0.5f)
            rhythmModeButton.onClickAction {
                changeModeCallback(GameModeName.RHYTHM_MODE) // Call to switch to RhythmMode
            }
            scene.addGameObject(rhythmModeButton)

            // Button to go to InstrumentalMode
            val instrumentalModeButton =
                Button(loadTextureBitmap("button.png", context), id = "instrumentalModeButton")
            instrumentalModeButton.setOriginPosition(
                y = -0.3f,
                x = 0f
            )  // Position button below the Rhythm button
            instrumentalModeButton.scale(0.5f)
            instrumentalModeButton.onClickAction {
                changeModeCallback(GameModeName.INSTRUMENTAL_MODE) // Call to switch to InstrumentalMode
            }
            scene.addGameObject(instrumentalModeButton)
        }
        return scene
    }
}
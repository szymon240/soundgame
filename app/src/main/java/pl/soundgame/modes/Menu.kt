package pl.soundgame.modes

import android.content.Context
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.Popup
import pl.soundgame.engine.loadTextureBitmap

class Menu(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {

            val popup = Popup(loadTextureBitmap("popupBackgound.png", context), popupText = "Testowy\n popup\nbaredzo długa linijka z dużą ilością zbędnego tekstu", popupAnswer = "Continue", id = "popup", duration = -1)

            val rhythmModeButton =
                Button(loadTextureBitmap("button.png", context), id = "rhythmModeButton")
            rhythmModeButton.setOriginPosition(
                y = 0.3f,
                x = 0f
            )  // Position button in the upper center
            rhythmModeButton.scale(0.5f)
            rhythmModeButton.onClickAction {
                changeModeCallback(GameModeName.RHYTHM) // Call to switch to RhythmMode
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
                changeModeCallback(GameModeName.INSTRUMENTAL) // Call to switch to InstrumentalMode
                //instrumentalModeButton.lock()
               // popup.setPopupCallback { instrumentalModeButton.unlock() }
              //  popup.showPopup()
            }
            scene.addGameObject(instrumentalModeButton, popup)

        }
        return scene
    }
}
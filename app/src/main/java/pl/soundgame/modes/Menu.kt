package pl.soundgame.modes

import android.content.Context
import android.util.Log
import pl.soundgame.R
import pl.soundgame.SoundGame
import pl.soundgame.connection.ConnectionStatus
import pl.soundgame.engine.Game
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.gameobjects.Popup
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap

class Menu(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    private var displayedConnectionStatus = ConnectionStatus.CONNECTING
    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        val settings = context.getString(R.string.settings)
        val rhythm = context.getString(R.string.rhythm)
        val instrumental = context.getString(R.string.instrumental)

        scene.setInitScene {
            val settings = TextBox(initialText = "$settings", id = "settings")
            val rhythm = TextBox(initialText = "$rhythm", id = "rhythm")
            val instrumental = TextBox(initialText = "$instrumental", id = "instrumental")

            settings.setOriginPosition(y = -0.06f, x = 0.5f)
            settings.scale(0.5f)
            rhythm.setOriginPosition(y = 0.57f, x = -0.5f)
            rhythm.scale(0.5f)
            instrumental.setOriginPosition(y = 0.6f, x = 0.5f)
            instrumental.scale(0.5f)
            scene.addGameObject(settings, rhythm, instrumental)

            val title = GameObject(loadTextureBitmap("title.png", context), "title")
            title.setOriginPosition(y = 0.8f)
            title.scale(0.5f)
            val popup = Popup(loadTextureBitmap("popupBackgound.png", context), popupText = "Testowy\n popup\nbaredzo długa linijka z dużą ilością zbędnego tekstu", popupAnswer = "Continue", id = "popup", duration = -1)


            val rhythmModeButton =
                Button(loadTextureBitmap("rhythmMode2.png", context), id = "rhythmModeButton")
            rhythmModeButton.setOriginPosition(
                y = 0.3f,
                x = -0.5f
            )  // Position button in the upper center
            rhythmModeButton.scale(0.4f)
            rhythmModeButton.onClickAction {
                changeModeCallback(GameModeName.RHYTHM) // Call to switch to RhythmMode
            }
            scene.addGameObject(rhythmModeButton)

            // Button to go to InstrumentalMode
            val instrumentalModeButton =
                Button(loadTextureBitmap("instrumental.png", context), id = "instrumentalModeButton")
            instrumentalModeButton.setOriginPosition(
                y = 0.3f,
                x = 0.5f
            )  // Position button below the Rhythm button
            instrumentalModeButton.scale(0.4f)
            instrumentalModeButton.onClickAction {
                changeModeCallback(GameModeName.INSTRUMENTAL) // Call to switch to InstrumentalMode
                //instrumentalModeButton.lock()
               // popup.setPopupCallback { instrumentalModeButton.unlock() }
              //  popup.showPopup()
            }

            val settingsButton =
                Button(loadTextureBitmap("settings.png", context), id = "settings Button")
            settingsButton.setOriginPosition(
                y = -0.33f,
                x = 0.5f
            )  // Position button below the Rhythm button
            settingsButton.scale(0.4f)
            settingsButton.onClickAction {
                changeModeCallback(GameModeName.SETTINGS) // Call to switch to InstrumentalMode
                //instrumentalModeButton.lock()
                // popup.setPopupCallback { instrumentalModeButton.unlock() }
                //  popup.showPopup()
            }

            val connectionStatusText = TextBox(initialText =  context.getString(R.string.connecting), width =  500, id = "connText")
            connectionStatusText.setOriginPosition(y = -0.8f)
            connectionStatusText.scale(0.3f)
            val connectionImage = GameObject(loadTextureBitmap("connection/connecting.png", context), id = "connImage")
            connectionImage.setOriginPosition(y = -0.8f, x = -0.5f )
            connectionImage.scale(0.1f)

            scene.addGameObject(title, instrumentalModeButton, popup, connectionStatusText, connectionImage, settingsButton )

        }


        scene.setBeforeDrawFrame {
            // Log.i("Menu",  SoundGame.CONNECTION_STATUS.toString() +  " " + displayedConnectionStatus.toString())
            if(displayedConnectionStatus != SoundGame.CONNECTION_STATUS){
                if(SoundGame.CONNECTION_STATUS == ConnectionStatus.FAILED){
                    scene.modifyGameObjectsById("connText") { obj ->
                                                val textBox = obj as? TextBox
                        if (textBox != null) {
                            textBox.displayedText = context.getString(R.string.connection_failed)
                        }
                    }
                    scene.modifyGameObjectsById("connImage") { obj ->
                        if (obj != null) {
                            obj.changeBaseBitmap(loadTextureBitmap("connection/failed.png", context))
                            displayedConnectionStatus = ConnectionStatus.FAILED
                        }
                    }
                    displayedConnectionStatus = ConnectionStatus.FAILED
                }
                else  if(SoundGame.CONNECTION_STATUS == ConnectionStatus.SUCCESS){
                    scene.modifyGameObjectsById("connText") { obj ->
                        val textBox = obj as? TextBox
                        if (textBox != null) {
                            textBox.displayedText = context.getString(R.string.connection_success)
                            Log.i("DEBUG", "TextBox found and ready to modify.")
                        }
                    }
                    scene.modifyGameObjectsById("connImage") { obj ->
                        if (obj != null) {
                            obj.changeBaseBitmap(loadTextureBitmap("connection/success.png", context))
                            displayedConnectionStatus = ConnectionStatus.SUCCESS
                        }
                    }
                }
            }
        }

        return scene
    }
}
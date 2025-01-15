package pl.soundgame.modes

import android.content.Context
import pl.soundgame.R
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import android.util.Log
import android.widget.Toast
import changeLocale
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.soundgame.MainActivity
import pl.soundgame.playerutils.UserManager
import pl.soundgame.engine.gameobjects.PopupTextfield
import pl.soundgame.engine.shapes.createTextTexture

class Settings(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    private val TAG = "Settings screen"

     private fun applyLocaleChange(languageCode: String) {
        context = changeLocale(context, languageCode)

        // Save the selected language to SharedPreferences
        val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("language_code", languageCode).apply()

    }

    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }
        scene.setInitScene {
            val nicknamePopup = PopupTextfield(
                popupAnswer1 = context.getString(R.string.nickname_popup_save),
                popupAnswer2 = context.getString(R.string.nickname_popup_cancel),
                background = loadTextureBitmap("popupBackground.png", context),
                id = "nicknamePopup",
                duration = -1,
                context = context as MainActivity
            )


            val engButton = Button(loadTextureBitmap("settings/uk_flag.png", context))
            engButton.setOriginPosition(y = 0.6f, x = 0.4f)
            engButton.scale(0.2f)
            engButton.onClickAction {
                applyLocaleChange("en")
                scene.refreshAll()
            }
            val plButton = Button(loadTextureBitmap("settings/pl_flag.png", context))
            plButton.setOriginPosition(y = 0.6f, x = -0.4f)
            plButton.scale(0.2f)
            plButton.onClickAction {
                applyLocaleChange("pl")
                scene.refreshAll()
            }


            val nickname = TextBox(initialText = "${context.getString(R.string.player_nick)} ${UserManager.getInstance(context).getNickname()}", size = 40f, width = 800)
            nickname.setOriginPosition(y = 0.35f, x = 0f, )
            nickname.scale(0.5f)



            val authorsInfo = TextBox(initialText = context.getString(R.string.authors_info), width = 600, height= 400, size = 40f)
            authorsInfo.setOriginPosition(y = -0.5f, x = 0f)
            authorsInfo.scale(0.5f)
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
            val changeNameButton = Button(createTextTexture(context.getString(R.string.nickname_change_button), size = 80f, background = loadTextureBitmap("button.png", context)))
            changeNameButton.setOriginPosition(y = 0.1f, x = 0f)
            changeNameButton.scale(0.25f)
            changeNameButton.onClickAction {
                nicknamePopup.showPopup()
                exitButton.lock()
                plButton.lock()
                engButton.lock()
                scene.refreshAll()
            }
            nicknamePopup.setPopupCallback1 {
                val inputText = nicknamePopup.inputText
                if (inputText.isNotBlank() && inputText.length <= 20) {
                    UserManager.getInstance(context).setNickname(inputText)
                    Log.i("Menu", "Nickname updated to: ${UserManager.getInstance(context).getNickname()}")
                    nicknamePopup.hidePopup()
                    exitButton.unlock()
                    plButton.unlock()
                    engButton.unlock()
                    nickname.displayedText = "${context.getString(R.string.player_nick)} ${UserManager.getInstance(context).getNickname()}"
                } else {
                    MainScope().launch {
                        val text = context.getString(R.string.incorrect_nickname)
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(context, text, duration)
                        toast.show()
                        Log.e(TAG, "Failed to change nickname")
                    }
                }
            }

            nicknamePopup.setPopupCallback2 {
                Log.i("Menu", "Nickname input cancelled")
                nicknamePopup.hidePopup()
                exitButton.unlock()
                plButton.unlock()
                engButton.unlock()
            }


            scene.addGameObject(exitButton, authorsInfo, plButton, engButton, changeNameButton, nickname, nicknamePopup)
        }

        return scene
    }
}

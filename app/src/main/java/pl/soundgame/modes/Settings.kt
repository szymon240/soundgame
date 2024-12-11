package pl.soundgame.modes

import android.content.Context
import pl.soundgame.R
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import android.content.res.Configuration
import java.util.Locale

class Settings(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {

    fun changeLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

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
            val engButton = Button(loadTextureBitmap("settings/uk_flag.png", context))
            engButton.setOriginPosition(y = 0.4f, x = 0.4f)
            engButton.scale(0.2f)
            engButton.onClickAction {
                applyLocaleChange("en")
                scene.refreshAll()
            }
            val plButton = Button(loadTextureBitmap("settings/pl_flag.png", context))
            plButton.setOriginPosition(y = 0.4f, x = -0.4f)
            plButton.scale(0.2f)
            plButton.onClickAction {
                applyLocaleChange("pl")
                scene.refreshAll()
            }
            val authorsInfo = TextBox(initialText = context.getString(R.string.authors_info), width = 280, size = 20f)
            authorsInfo.setOriginPosition(y = -0.4f, x = 0f)
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
            scene.addGameObject(exitButton, authorsInfo, plButton, engButton)
        }

        return scene
    }
}

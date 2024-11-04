package pl.soundgame.modes

import android.content.Context
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground

class Menu(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        return scene
    }
}
package pl.soundgame.modes

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.loadTextureBitmap

class Empty(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {

    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }
        scene.setInitScene {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                changeModeCallback(GameModeName.MENU)
            }
        }

        return scene
    }
}
package pl.soundgame

import android.content.Context
import android.util.Log
import pl.soundgame.engine.Game
import pl.soundgame.engine.Scene
import pl.soundgame.modes.GameMode
import pl.soundgame.modes.GameModeName
import pl.soundgame.modes.InstrumentalMode
import pl.soundgame.modes.Menu
import pl.soundgame.modes.RhythmMode


internal class SoundGame(context: Context) : Game() {
    override var mScene: Scene
    private var gameMode: GameMode
    private var context: Context
    private var gameModeName: GameModeName
    private var TAG = "SoundGame Main Object"
    private var changeModeCallback: (GameModeName) -> Unit = { mode -> changeMode(mode)}
    private var rounds = 8
    init {
        this.context = context
        gameMode = Menu(this.context, changeModeCallback)

        gameModeName = GameModeName.MENU
        mScene = gameMode.returnGameModeScene()
        changeMode(GameModeName.MENU)
    }

    fun changeMode(newMode: GameModeName) {
        gameMode = when (newMode) {
            GameModeName.MENU -> Menu(this.context, changeModeCallback)
            GameModeName.RHYTHM_MODE -> RhythmMode(rounds, this.context, changeModeCallback)
            GameModeName.INSTRUMENTAL_MODE -> InstrumentalMode(this.context, changeModeCallback)
            //GameModeName.ANOTHER_MODE -> AnotherMode(this.context)

        }
        Log.i(TAG, "Swaped mode to: ${newMode.name}")
        gameModeName = newMode
        mScene = gameMode.returnGameModeScene()
        mScene.loadScene()
    }
}


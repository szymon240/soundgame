package pl.soundgame

import android.content.Context
import android.util.Log
import pl.soundgame.engine.Game
import pl.soundgame.engine.Scene
import pl.soundgame.modes.GameMode
import pl.soundgame.modes.GameModeName
import pl.soundgame.modes.InstrumentalMode
import pl.soundgame.modes.Menu
import pl.soundgame.modes.RythmMode


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
        gameMode = InstrumentalMode(rounds, this.context, changeModeCallback)

        gameModeName = GameModeName.INSTRUMENTAL_MODE
        mScene = gameMode.returnGameModeScene()
    }

    fun changeMode(newMode: GameModeName) {
        gameMode = when (newMode) {
            GameModeName.MENU -> Menu(this.context, changeModeCallback)
            GameModeName.RYTHM_MODE -> RythmMode(rounds, this.context, changeModeCallback)
            GameModeName.INSTRUMENTAL_MODE -> InstrumentalMode(rounds, this.context, changeModeCallback)
            //GameModeName.ANOTHER_MODE -> AnotherMode(this.context)

        }
        Log.i(TAG, "Swaped mode to: ${newMode.name}")
        gameModeName = newMode
        mScene = gameMode.returnGameModeScene()
    }
}


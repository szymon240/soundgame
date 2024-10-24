package pl.soundgame

import android.content.Context
import pl.soundgame.engine.Game
import pl.soundgame.engine.Scene
import pl.soundgame.modes.GameMode
import pl.soundgame.modes.RythmMode


internal class SoundGame(context: Context) : Game() {
    override var mScene: Scene
    private var gameMode: GameMode
    private var context: Context

    init {
        this.context = context
        gameMode = RythmMode(this.context)
        mScene = gameMode.returnGameModeScene()
    }

    fun changeMode(){
        TODO("implement change mode")

    }
}


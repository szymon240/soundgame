package pl.soundgame.modes

import pl.soundgame.SoundPlayer
import pl.soundgame.engine.Scene

abstract class GameMode {
    abstract fun returnGameModeScene(): Scene
}
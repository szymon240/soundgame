package pl.soundgame

import pl.soundgame.engine.Game
import pl.soundgame.engine.Scene


internal class SoundGame(initialScene: Scene) : Game() {
    override var mScene: Scene
    var someQuestion = "pytanko"
    private var audioPlayer: Int = 0

    init {
        mScene = initialScene
    }
}


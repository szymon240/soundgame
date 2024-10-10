package pl.soundgame.engine.gameobjects.gameobjectstates

import pl.soundgame.engine.gameobjects.GameObject

class GameObjectDefaultState(mGameObject: GameObject) : GameObjectState(mGameObject) {
    init{
        this.mGameObject = mGameObject
        this.mStateName = "Default"
    }
}
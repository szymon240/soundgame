package pl.soundgame.engine.gameobjects.gameobjectstates

import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.gameobjects.Hitbox
import pl.soundgame.engine.shapes.Sprite

abstract class GameObjectState(mGameObject: GameObject) {
    protected var mGameObject: GameObject
    protected var mStateName: String

    init{
        this.mGameObject = mGameObject
        this.mStateName = "None"
    }

    fun draw(pShaderProgram: Int, scratch: FloatArray, pSprite: Sprite ){
        pSprite.draw(pShaderProgram, scratch)
    }

    fun click(x: Int, y: Int, hitbox: Hitbox ){
        return
    }
}
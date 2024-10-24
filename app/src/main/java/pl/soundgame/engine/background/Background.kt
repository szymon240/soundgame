package pl.soundgame.engine.background

import pl.soundgame.engine.ShaderProgram
import pl.soundgame.engine.shapes.Drawable

abstract class Background {
    protected abstract var mShaderProgram: ShaderProgram
    protected abstract var mObjects: MutableList<Drawable>
    private var beforeDrawFrame: (() -> Unit)? = null
    private var afterDrawFrame: (() -> Unit)? = null

    fun draw(vPMatrix: FloatArray ){
        beforeDrawFrame?.invoke()
        for( gameObject in mObjects){
            gameObject.draw(mShaderProgram.getProgram(),vPMatrix)
        }
        afterDrawFrame?.invoke()
    }

    fun setBeforeDrawFrame(fn: () -> Unit){
        beforeDrawFrame = fn
    }

    fun setAfterDrawFrame(fn: () -> Unit){
        afterDrawFrame = fn
    }

    /**
     * Adds GameObject to the background
     *
     * @param pGameObject object to be added, can be multiple separated with ,
     */
    fun addGameObject(vararg pGameObjects: Drawable){
        for(obj in pGameObjects) {
            mObjects.add(obj)
        }
    }
}
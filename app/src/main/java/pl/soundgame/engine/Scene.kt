package pl.soundgame.engine

import pl.soundgame.engine.gameobjects.GameObject

/**
 * Scene - abstract class for storing and managing elements of segment of the game. To create concrete
 * scenes implement this class.
 *
 * @author Adam Czyżak
 */

class Scene {
    private var mObjects: MutableList<GameObject> = mutableListOf<GameObject>()
    private var mInitScene: (() -> Unit)? = null
    var id: String = ""
        get() = field
        set(value) {
            field = value
        }

    /**
     * Adds GameObject to the scene
     *
     * @param pGameObject object to be added, can be multiple separated with ,
     */
    fun addGameObject(vararg pGameObjects: GameObject){
        for(obj in pGameObjects) {
            mObjects.add(obj)
        }
    }

    /**
     * Calling function calls draw on every object in the scene
     *
     * @param shaderProgram Int value being ShaderPrograms id - that program will be used in drawing process
     * @param vPMatrix FloatArray(16) - product of view and perspective matrix multiplication
     */
    fun draw(shaderProgram: Int, vPMatrix: FloatArray ){
        for( gameObject in mObjects){
            gameObject.draw(shaderProgram,vPMatrix)
        }
    }

    /**
     * Function calls click() method on GameObjects
     *
     * @param x x position of click on the screen
     * @param y y position of click on the screen
     */
    fun clickHandle(x: Float, y: Float){
        for( gameObject in mObjects){
            gameObject.click(x, y)
        }
    }

    /**
     * Method should contain process of loading every object to create specific scene     *
     */
    fun loadScene(){
        if(mInitScene != null){
            mInitScene?.invoke()
        }
    }


    /**
     * Sets init scene
     *
     * @param function that fill create scene
     */
    fun setInitScene(fn: ()->Unit){
        mInitScene = fn
    }

    fun bindClickAction(function: () -> Unit, id: String){
        for(gameObject in mObjects){
            if(gameObject.getId() == id){
                gameObject.setClickAction {function.invoke()}
            }
        }
    }

    fun executeFunction(foo:  () -> Unit ){foo()}
}
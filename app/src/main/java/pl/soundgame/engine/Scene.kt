package pl.soundgame.engine

import android.opengl.GLES20
import android.util.Log
import pl.soundgame.engine.background.Background
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.GameObject
import java.util.Objects

/**
 * Scene - abstract class for storing and managing elements of segment of the game. To create concrete
 * scenes implement this class.
 *
 * @author Adam Czyżak
 */

class Scene {
    private var mObjects: MutableList<GameObject> = mutableListOf<GameObject>()
    private var mInitScene: (() -> Unit)? = null
    private var mInitBackground: (() -> Background)? = null
    private var mNewInitialization: Boolean = false
    private lateinit var mBackground: Background
    private var backgroundInitialized: Boolean = false
    private var beforeDrawFrame: (() -> Unit)? = null
    private var afterDrawFrame: (() -> Unit)? = null
    private var timesRefreshed: Int = 0
    private var framesPassed: UInt = 0u
    var id: String = ""

    fun setBackground(pBackground: () -> Background){
        mInitBackground = pBackground
        mNewInitialization = true
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
        beforeDrawFrame?.invoke()
        if(mNewInitialization){
            mBackground = mInitBackground!!.invoke()
            backgroundInitialized = true
            mNewInitialization = false
        }
        if(backgroundInitialized) mBackground.draw()

        for( gameObject in mObjects){
            gameObject.draw(shaderProgram,vPMatrix)
        }
        afterDrawFrame?.invoke()
        if ( framesPassed < 40u && framesPassed % 10u == 0u )
        {
            timesRefreshed++
            this.refreshAll()
        }
        framesPassed++
        //GLES20.glDisable(GLES20.GL_BLEND)
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

    fun refreshAll(){
        for( obj in mObjects){
            obj.refresh()
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
                gameObject.onClickAction {function.invoke()}
            }
        }
    }

    fun removeGameObject(id: String){
        mObjects.removeIf { obj -> obj.getId() == id }
    }

    /**
     * Finds and returns all GameObjects with the given id.
     *
     * @param id The id to search for.
     * @return List of matching GameObjects.
     */
    fun findGameObjectsById(id: String): List<GameObject> {
        return mObjects.filter { it.getId() == id }
    }

    /**
     * Modifies the properties of all GameObjects with the given id.
     *
     * @param id The id to search for.
     * @param modifyFn A function to modify the GameObject's properties.
     */
    fun modifyGameObjectsById(id: String, modifyFn: (GameObject) -> Unit) {
        for (gameObject in mObjects) {
            if (gameObject.getId().equals(id)) {
                Log.i("Scene", "Changing object ${gameObject.getId()}")
                modifyFn(gameObject)
            }
        }
    }

    fun setBeforeDrawFrame(fn: () -> Unit){
        beforeDrawFrame = fn
    }

    fun setAfterDrawFrame(fn: () -> Unit){
        afterDrawFrame = fn
    }

    fun lockAllButtons(){
        for(obj in mObjects){
            if(obj is Button){
                obj.lock()
            }
        }
    }

    fun unlockAllButtons(){
        for(obj in mObjects){
            if(obj is Button){
                obj.unlock()
            }
        }
    }
}
package pl.soundgame.engine

import android.opengl.GLES20
import pl.soundgame.engine.background.Background
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
    private var mInitBackground: (() -> Background)? = null
    private var mNewInitialization: Boolean = false
    private lateinit var mBackground: Background
    private var backgroundInitialized: Boolean = false

    var id: String = ""
        get() = field
        set(value) {
            field = value
        }

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
        if(mNewInitialization){
            mBackground = mInitBackground!!.invoke()
            backgroundInitialized = true
            mNewInitialization = false
        }
        if(backgroundInitialized) mBackground.draw()
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        for( gameObject in mObjects){
            gameObject.draw(shaderProgram,vPMatrix)
        }
        GLES20.glDisable(GLES20.GL_BLEND)
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
                gameObject.onClickAction {function.invoke()}
            }
        }
    }
}
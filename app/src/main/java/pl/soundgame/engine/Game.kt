package pl.soundgame.engine

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import pl.soundgame.engine.Scene
import pl.soundgame.engine.ShaderProgram
import pl.soundgame.engine.background.Background

abstract class Game {
    private var width: Float = 0.0f
    private var height: Float = 0.0f
    private  var ratio: Float = 0.0f
    abstract var mScene: Scene
    lateinit var mShaderProgram: ShaderProgram
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private var sceneStorage = mutableMapOf<String, Scene>()
    private var beforeDrawFrame: (() -> Unit)? = null
    private var afterDrawFrame: (() -> Unit)? = null
    private var afterCreateSurface: (() -> Unit)? = null

    private val TAG = "Game"

    fun onCrateSurface(){
        Log.d(TAG, "Initializing game" )
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        mScene.loadScene()
        if(afterCreateSurface != null) {afterCreateSurface?.invoke()}

    }

    fun setShaderProgram(shaderProgram: ShaderProgram){
        mShaderProgram = shaderProgram
    }

    fun onSurfaceChanged(width: Int, height: Int){
        this.width = width.toFloat()
        this.height = height.toFloat()
        this.ratio = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        //Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    fun onDrawFrame(){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 6f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        if(beforeDrawFrame != null) {beforeDrawFrame?.invoke()}
        mScene.draw(mShaderProgram.getProgram(), vPMatrix)
        if(afterDrawFrame != null) {afterDrawFrame?.invoke()}
    }

    fun clickHandle(x: Float, y: Float){
        // Convert screen coordinates to normalized device coordinates (NDC)
        val xInClipSpace = (2.0f * x / width - 1.0f) * ratio
        val yInClipSpace = 1.0f - 2.0f * y / height

        Log.i(TAG, "pressed: x = $xInClipSpace, y = $yInClipSpace")
        mScene.clickHandle(xInClipSpace, yInClipSpace)
    }
    fun setBeforeDrawFrame(fn: () -> Unit){
        beforeDrawFrame = fn
    }

    fun setAfterDrawFrame(fn: () -> Unit){
        afterDrawFrame = fn
    }

    fun setAfterCreateSurface(fn: () -> Unit){
        afterCreateSurface = fn
    }

    fun storeScene(id: String,scene: Scene){
        this.sceneStorage.put(id, scene)
    }

    fun loadStoredScene(id: String){
        if(sceneStorage[id]!=null) {
            this.switchScene(sceneStorage[id]!!)
        }
    }

    fun switchScene(scene: Scene){
        mScene = scene
        mScene.loadScene()
    }
}
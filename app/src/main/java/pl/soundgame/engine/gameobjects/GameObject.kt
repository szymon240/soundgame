package pl.soundgame.engine.gameobjects

import android.graphics.Bitmap
import android.opengl.Matrix
import android.util.Log
import pl.soundgame.engine.shapes.Drawable
import pl.soundgame.engine.shapes.Hitbox
import pl.soundgame.engine.shapes.Sprite


/**
 * Class for single object to be displayed on user screen
 *
 * @constructor Takes Bitmap as mandatory argument and id as optional, Default position is on the
 * center of world space (starting point x: -0.5, y: 0.5, 1.0f width and height)
 *
 * @author Adam Czyżak
 */

open class GameObject(bitmap: Bitmap, id: String = "") : Drawable() {
    protected var mSprite: Sprite
    override val mMatrix = FloatArray(16)
    protected val mMatrixFrameChange = FloatArray(16)
    private var mId: String =""
    private var mHitbox: Hitbox
    private var mPosition = arrayOf(0.0f, 0.0f, 0.0f)  // Position of the GameObject
    protected var clickAction: (() -> Unit)? = null
    private var width: Float
    private var height: Float

    protected var baseBitmap: Bitmap

    var visible = true

    init {
        baseBitmap = bitmap
        this.mSprite = Sprite(bitmap)

        Matrix.setIdentityM(mMatrix, 0)
        Matrix.setIdentityM(mMatrixFrameChange, 0)

        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val initialWidth = 1.0f * aspectRatio
        val initialHeight = 1.0f
        width = initialWidth
        height = initialHeight

        // Adjusted hitbox positioning based on GameObject size
        this.mHitbox = Hitbox(-0.5f * width, 0.5f * height, initialWidth, initialHeight)
    }

    /**
     * Prints in logcat base info about object
     *
     */
    fun logObjectInfo(){
        Log.i("Sprite: ${mId}", "Object ${mId} exists in Scene\n" +
                "visible: $visible\n" +
                "click function bound: ${clickAction != null}\n")
    }

    open fun beforeDraw() {}
    override fun draw(shaderProgram: Int, vPMatrix: FloatArray) {
        if (visible) {
            val scratch = FloatArray(16)
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, mMatrixFrameChange, 0)
            Matrix.multiplyMM(scratch, 0, mMatrix, 0, scratch, 0)
            mSprite.draw(shaderProgram, scratch)
        }
        Matrix.setIdentityM(mMatrixFrameChange, 0)
    }
    fun swapSprite(newBitmap: Bitmap){
        mSprite.swapImage(newBitmap)
    }
    fun setId(pId: String){
        this.mId = pId
    }

    fun getId(): String{
        return mId
    }
    fun onClickAction(function: () -> Unit) {
        this.clickAction = function
    }

    fun removeClickAction() {
        this.clickAction = null
    }

    open fun afterClickDetected(){}

    open fun click(x: Float, y: Float): Boolean {
        //mHitbox.logInfo(mId)
        return if (mHitbox.isClicked(x, y)) {
            clickAction?.invoke()
            afterClickDetected()
            true
        } else {
            false
        }
    }

    fun scale(ratio: Float) {
        Matrix.scaleM(mMatrix, 0, ratio, ratio, ratio)
        width *= ratio
        height *= ratio
        updateHitbox()
    }

    fun scale(x: Float = 1.0f, y: Float = 1.0f, z: Float = 1.0f) {
        Matrix.scaleM(mMatrix, 0, x, y, z)
        width *= x
        height *= y
        updateHitbox()
    }

    fun translate(x: Float, y: Float) {
        Matrix.translateM(mMatrix, 0, x, y, 0.0f)
        mPosition[0] += x
        mPosition[1] += y
        updateHitbox()
    }

    fun setOriginPosition(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f) {
        Matrix.setIdentityM(mMatrix, 0)
        Matrix.translateM(mMatrix, 0, x, y, z)
        mPosition[0] = x
        mPosition[1] = y
        updateHitbox()
    }

    private fun updateHitbox() {
        // Update the hitbox based on the GameObject's position and size
        println("${ mPosition[0] } ${ mPosition[1] }")

        if(mPosition[0] < 0.0f) {
            val newX = mPosition[0]
            val newY = mPosition[1] + height / 2
            mHitbox.updatePosition(newX, newY)
            mHitbox.updateSize(width, height)
        }else if(mPosition[0] == 0.0f){
            val newX = mPosition[0] - width /2
            val newY = mPosition[1] + height / 2
            mHitbox.updatePosition(newX, newY)
            mHitbox.updateSize(width, height)
        } else{

            val newX = mPosition[0] - width
            val newY = mPosition[1] + height / 2
            //Log.i("${mId}"," ${newX}, ${width} ${newX + width}" )
            mHitbox.updatePosition(newX, newY)
            mHitbox.updateSize(width, height)
        }
    }
}
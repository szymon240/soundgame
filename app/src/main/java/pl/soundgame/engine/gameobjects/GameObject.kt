package pl.soundgame.engine.gameobjects

import android.graphics.Bitmap
import android.opengl.Matrix
import android.util.Log
import pl.soundgame.engine.gameobjects.gameobjectstates.GameObjectDefaultState
import pl.soundgame.engine.gameobjects.gameobjectstates.GameObjectState
import pl.soundgame.engine.shapes.Sprite


/**
 * Class for single object to be displayed on user screen
 *
 * @constructor Takes Bitmap as mandatory argument and id as optional, Default position is on the
 * center of world space (starting point x: -0.5, y: 0.5, 1.0f width and height)
 *
 * @author Adam Czyżak
 */

class GameObject(bitmap: Bitmap, id: String = "") {
    private var mSprite: Sprite
    private val mMatrix = FloatArray(16)
    private val mMatrixFrameChange = FloatArray(16)
    private var mGameObjectState: GameObjectState
    private var mId: String
    private var mHitbox: Hitbox
    private var mPostion = arrayOf(0.0f,0.0f,0.0f)
    private var clickAction:  (() -> Unit)? = null
    var visible = true

    init {
        this.mGameObjectState = GameObjectDefaultState(this)
        this.mSprite = Sprite(bitmap)
        Matrix.setIdentityM(mMatrix, 0)
        Matrix.setIdentityM(mMatrixFrameChange, 0)
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        //this.mHitbox = Hitbox(-0.5f * aspectRatio, -0.5f, 1f * aspectRatio, 1f)
        this.mId = id

        val initialWidth = 1.0f * aspectRatio
        val initialHeight = 1.0f

        this.mHitbox  = Hitbox(-0.5f * aspectRatio, -0.5f, initialWidth, initialHeight)

    }

    private fun updateHitbox() {
        // Translation
        val posX = mMatrix[12]
        val posY = mMatrix[13]

        // Scaling factors
        val scaleX = mMatrix[0] // Scale in the x direction
        val scaleY = mMatrix[5] // Scale in the y direction

        // Update hitbox dimensions using current scaling and translation
        val aspectRatio = mSprite.aspectRatio
        val worldHeight = scaleY
        val worldWidth = worldHeight * aspectRatio * scaleX

        val halfWidth = worldWidth / 2.0f
        val halfHeight = worldHeight / 2.0f

        mHitbox = Hitbox(posX - halfWidth, posY - halfHeight, worldWidth, worldHeight)
    }

    fun logObjectInfo(){
        Log.i("Sprite: ${mId}", "Object ${mId} exists in Scene\n" +
                "visible: $visible\n" +
                "click function bound: ${clickAction!=null}\n")
    }

    /**
     * draw method call prepares matrix for drawing and calls .draw method on sprite. Then frameChange matrix is reset
     *
     * @param shaderProgram is an Int handle to chosen shader program
     * @param vPMatrix is an FloatArray(16) being matrix of V * P
     */
    fun draw(shaderProgram: Int, vPMatrix: FloatArray ) {
        if(visible) {
            val scratch = FloatArray(16)
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, mMatrixFrameChange, 0)
            Matrix.multiplyMM(scratch, 0, mMatrix, 0, scratch, 0)
            mGameObjectState.draw(shaderProgram, scratch, mSprite)
        }
        Matrix.setIdentityM(mMatrixFrameChange, 0)
    }
    /**
     * Set function to be called after click
     *
     * @param function lambda function called when click is detected
     */
    fun setClickAction(function: () -> Unit) {
        this.clickAction = function
    }

    /**
     * Removes function bound to the click
     */
    fun removeClickAction(){
        this.clickAction = null
    }

    /**
     * Checks if object has been clicked by the user (doesn't check if it's covered)
     *
     * @param x - x position in the world space
     * @param y - y position in the world space
     * @return true if click has been detected, false if object hasn't been clicked
     */
    fun click(x: Float, y: Float): Boolean {
        mHitbox.logInfo(mId)
        return if (mHitbox.isClicked(x, y)) {
            Log.i("GameObject: $mId", "Click detected!")
            clickAction?.invoke()
            true
        } else {
            Log.i("GameObject: $mId", "Click not detected!")
            false
        }
    }


    fun setId(pId: String){
        this.mId = pId
    }

    fun getId(): String{
        return mId
    }

    fun swapSprite(newBitmap: Bitmap){
        mSprite.swapImage(newBitmap)
    }

    fun rotate(angle: Float){
        val rotationMatrix = FloatArray(16)
        Matrix.setIdentityM(rotationMatrix, 0)
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)
        //Matrix.multiplyMM(mMatrixFrameChange, 0, mMatrix , 0, rotationMatrix, 0)
        Matrix.multiplyMM(mMatrixFrameChange, 0, mMatrixFrameChange , 0, rotationMatrix, 0)
    }

    fun scale(ratio: Float){

        //mHitbox = Hitbox(mHitbox.X * ratio, mHitbox.Y * ratio, mHitbox.width * ratio, mHitbox.height * ratio)
        Matrix.scaleM(mMatrix, 0,mMatrix,0, ratio, ratio, ratio)
        updateHitbox()
    }

    fun scale(x: Float = 1.0f, y: Float = 1.0f, z: Float = 1.0f){
        val aspectRatio = mSprite.aspectRatio  // Get aspect ratio from the sprite

        // Adjust scaling to preserve aspect ratio
        val actualX = x * aspectRatio
        //mHitbox = Hitbox(mHitbox.X * actualX, mHitbox.Y * y, mHitbox.width * actualX, mHitbox.height * y)
        Matrix.scaleM(mMatrix, 0, actualX, y, z)
        updateHitbox()
    }

    fun translate(x: Float, y: Float) {
        Matrix.translateM(mMatrix, 0, x, y, 0.0f)
        updateHitbox() // Ensure hitbox reflects translation
    }

    fun setOriginPosition(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f){
        val transformationMatrix = FloatArray(16)
        val scratch = FloatArray(16)
        Matrix.setIdentityM(scratch, 0)
        Matrix.setIdentityM(transformationMatrix, 0)
        Matrix.translateM(transformationMatrix, 0, x, y, z)
        //Matrix.multiplyMM(mMatrixFrameChange, 0, mMatrix, 0, transformationMatrix, 0)
        Matrix.multiplyMM(mMatrix, 0, scratch, 0, transformationMatrix, 0)

        //mHitbox = Hitbox(x -0.5f, y + 0.5f, mHitbox.width, mHitbox.height)
        updateHitbox()
    }

}
package pl.soundgame.engine.gameobjects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.opengl.Matrix

/**
 * Class for creating and managing buttons
 *
 * @constructor
 * TODO
 *
 * @param bitmap bitmap being backgorund of buton
 * @param id id for using in collections
 * @param alternateBitmap bitmap to be used in animation after button is pressed. If null a darken version of original bitmap will be used
 */
class Button(bitmap: Bitmap, id: String = "", alternateBitmap: Bitmap? = null) : GameObject(bitmap, id){
    private var isBlocked: Boolean = false
    private var animationFrameCounter: Int = 0
    private var storeClickAction: (() -> Unit)? = null
    private var wasClicked: Boolean = false
    private var wasSwaped = false
    private var wasSetAlternate = false
    private var baseBitmapBackup: Bitmap
    protected var alternateBitmap: Bitmap
    init{
        baseBitmapBackup = bitmap
        if(alternateBitmap == null){
            wasSetAlternate = true
            this.alternateBitmap = darkenBitmap(bitmap)
        }else{
            this.alternateBitmap = alternateBitmap
        }
    }

    /**
     * Get information if button is locked from being clicked
     *
     * @return true if button is locked - cannot be clicked, else false
     */
    fun isLocked(): Boolean {
        return isBlocked
    }

    override fun click(x: Float, y: Float): Boolean {
        if(isBlocked) return false
        return super.click(x, y)
    }

    /**
     * Change lock state to opposite of current state
     */
    fun toggleLock(){
        if (!isBlocked){
            isBlocked = !isBlocked
            swapSprite(alternateBitmap)
            if(clickAction != null) storeClickAction = clickAction!!
            clickAction = null
        }else{
            isBlocked = !isBlocked
            swapSprite(baseBitmap)
            if(storeClickAction != null) clickAction = storeClickAction
            storeClickAction = null
        }
    }

    /**
     * Locks button so it cannot be clicked.
     */
    fun lock(){
        if (!isBlocked){
            isBlocked = !isBlocked
            wasSwaped = true; alternateBitmap.let { mSprite.swapImage(it) }
            if(clickAction != null) storeClickAction = clickAction
            clickAction = null
        }
    }

    /**
     * Unlocks button so it can be clicked
     */
    fun unlock() {
        if (isBlocked){
            isBlocked = !isBlocked
            wasSwaped = false
            animationFrameCounter = 1
            wasClicked = true
            if(storeClickAction != null) clickAction = storeClickAction
            storeClickAction = null

        }
    }

    override fun draw(shaderProgram: Int, vPMatrix: FloatArray) {
        if(wasClicked){
            if(!wasSwaped) { wasSwaped = true; alternateBitmap.let { mSprite.swapImage(it) }
            }
            animationFrameCounter--
            if(animationFrameCounter == 0){
                wasClicked = false
                wasSwaped = false
                mSprite.swapImage(this.baseBitmap)
            }
        }
        super.draw(shaderProgram, vPMatrix)
    }

    override fun afterClickDetected(){
        animationFrameCounter = 10
        wasClicked = true
    }

    private fun darkenBitmap(inputBitmap: Bitmap, darkenFactor: Float = 0.2f): Bitmap {
        val clampedFactor = darkenFactor.coerceIn(0f, 1f)
        val darkenedBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
        val canvas = Canvas(darkenedBitmap)

        canvas.drawBitmap(inputBitmap, 0f, 0f, null)
        val paint = Paint()
        paint.color = Color.argb((clampedFactor * 255).toInt(), 0, 0, 0)
        canvas.drawRect(0f, 0f, inputBitmap.width.toFloat(), inputBitmap.height.toFloat(), paint)
        return darkenedBitmap
    }

    override fun changeBaseBitmap(newBitmap: Bitmap){
        super.swapSprite(newBitmap)
        baseBitmap = newBitmap
        alternateBitmap = darkenBitmap(newBitmap)
    }

    fun changeToBase(){
        swapSprite(baseBitmap)
    }
}
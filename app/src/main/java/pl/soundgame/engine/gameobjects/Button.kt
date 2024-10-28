package pl.soundgame.engine.gameobjects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.opengl.Matrix


class Button(bitmap: Bitmap, id: String = "", alternateBitmap: Bitmap? = null) : GameObject(bitmap, id){
    private var isBlocked: Boolean = false
    private var animationFrameCounter: Int = 0
    private var storeClickAction: (() -> Unit)? = null
    private var wasClicked: Boolean = false
    private var wasSwaped = false

    protected var alternateBitmap: Bitmap
    init{
        if(alternateBitmap == null){
            this.alternateBitmap = darkenBitmap(bitmap)
        }else{
            this.alternateBitmap = alternateBitmap
        }
    }
    fun isLocked(): Boolean {
        return isBlocked
    }

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
    fun lock(){
        if (!isBlocked){
            isBlocked = !isBlocked
            swapSprite(alternateBitmap)
            if(clickAction != null) storeClickAction = clickAction!!
            clickAction = null
        }
    }

    fun unlock() {
        if (isBlocked){
            isBlocked = !isBlocked
            swapSprite(baseBitmap)
            if(storeClickAction != null) clickAction = storeClickAction
            storeClickAction = null
        }
    }

    override fun draw(shaderProgram: Int, vPMatrix: FloatArray) {
        if(wasClicked){
            if(!wasSwaped) { wasSwaped = true; alternateBitmap?.let { mSprite.swapImage(it) }}
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

    fun darkenBitmap(inputBitmap: Bitmap, darkenFactor: Float = 0.2f): Bitmap {
        val clampedFactor = darkenFactor.coerceIn(0f, 1f)
        val darkenedBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
        val canvas = Canvas(darkenedBitmap)

        canvas.drawBitmap(inputBitmap, 0f, 0f, null)
        val paint = Paint()
        paint.color = Color.argb((clampedFactor * 255).toInt(), 0, 0, 0)
        canvas.drawRect(0f, 0f, inputBitmap.width.toFloat(), inputBitmap.height.toFloat(), paint)
        return darkenedBitmap
    }
}
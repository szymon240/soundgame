package pl.soundgame.engine.gameobjects

import android.content.Context
import android.graphics.Bitmap
import pl.soundgame.engine.shapes.Color
import pl.soundgame.engine.shapes.createTextTexture

class TextBox(
    id: String = "",
    initialText: String = "",
    width: Int = 256,
    height: Int = 256,
    size: Float = 32f,
    color: Color = Color.WHITE,
    background: Bitmap? = null
) : GameObject(
    createTextTexture(initialText, width, height, size, color, background),
    id
) {
    var displayedText: String = initialText
        get() = field
        set(value){
            field = value
            updateBitmap()
        }
    var width: Int = width
        get() = field
        set(value) {
            field = value
            updateBitmap()
        }
    var height: Int = height
        get() = field
        set(value) {
            field = value
            updateBitmap()
        }
    var size: Float = size
        get() = field
        set(value) {
            field = value
            updateBitmap()
        }
    var color: Color = color
        get() = field
        set(value) {
            field = value
            updateBitmap()
        }

    var background: Bitmap? = background
        get() = field
        set(value) {
            field = value
            updateBitmap()
        }

    private fun updateBitmap(){
        baseBitmap = createTextTexture(displayedText, width, height, size, color, background)
        swapSprite(baseBitmap)
    }
}
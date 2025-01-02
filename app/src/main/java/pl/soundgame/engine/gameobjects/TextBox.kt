package pl.soundgame.engine.gameobjects

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import pl.soundgame.engine.shapes.Color
import pl.soundgame.engine.shapes.createTextTexture

/**
 * Class for displaying some text
 *
 * @constructor
 * Sets up text texture
 *
 * @param id identification for use in collections
 * @param initialText text to initialize TextBox with.
 * @param width Desired maximum width of the text, ignored if background is setup
 * @param height Desired maximum height of the text, ignored if background is setup
 * @param size Text font size in points
 * @param color Color of text font
 * @param background Bitmap image to be displayed behind the text, if null the background will be transparent but still clickable
 *
 * @author Adam Czyżak
 */

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
        set(value){
            field = value
            updateBitmap()
        }
    var width: Int = width
        set(value) {
            field = value
            updateBitmap()
        }
    var height: Int = height
        set(value) {
            field = value
            updateBitmap()
        }
    var size: Float = size
        set(value) {
            field = value
            updateBitmap()
        }
    var color: Color = color
        set(value) {
            field = value
            updateBitmap()
        }

    var background: Bitmap? = background
        set(value) {
            field = value
            updateBitmap()
        }

    private fun updateBitmap(){
        Log.i("DEBUG", "Updating TextBox bitmap with text: $displayedText")

        baseBitmap = createTextTexture(displayedText, width, height, size, color, background)
        swapSprite(baseBitmap)
    }
}
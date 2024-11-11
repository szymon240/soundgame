package pl.soundgame.engine.gameobjects

import android.graphics.Bitmap
import pl.soundgame.engine.shapes.Color

/**
 * Class for displaying temporary windows with some info to user, can be dismissed with button click or after some time
 *
 * @property duration  time in frames the popup will stay visible, will be only affected by button if -1
 * @constructor
 * sets-up all elements of the popup
 *
 * @param background  background bitmap image for given popup
 * @param popupText  text that will appear in the top of popup - required parameter
 * @param popupAnswer  text on a button to skip such popup
 * @param id identification for one popup for using in collections
 */
class Popup(background: Bitmap, popupText: String, popupAnswer: String = "", val duration: Int = -1, id: String = "") : GameObject(bitmap = background , id= id) {
    private val timedPopup: Boolean
    private var popupOn: Boolean = false
    private var currentDuration = duration
    private var popupCallback: (() -> Unit)? = null
    private var framesOn: Int = 0

    /**
     * Text on top of popup
     */
    var popupText: String = popupText
        get() = field
        set(value){
            field = value
        }

    /**
     * Text on the bottom of popup - can be clicked to dismiss a popup
     */
    var popupAnswer: String = popupAnswer
        get() = field
        set(value){
            field = value
        }

    public var popupTextBox: TextBox
    private var answerButton: TextBox

    /**
     * Sets function that will be called after popup disappears
     *
     * @param func Function to be called after popup disappears
     */
    fun setPopupCallback(func: ()->Unit){
        popupCallback = func
    }
    init{
        if(duration > 0) timedPopup = true
        else timedPopup = false

        this.scale(0.75f)

        popupTextBox = TextBox(id = "${id} - Textbox",  size = 32f, width = 300, initialText = popupText)
        popupTextBox.setOriginPosition(y = 0.1f)
        popupTextBox.scale(0.5f)

        answerButton = TextBox(id = "${id} - Answer", size = 32f, initialText = popupAnswer, color = Color.YELLOW)
        answerButton.setOriginPosition(y = -0.2f)
        answerButton.scale(0.5f)


    }

    /**
     * Standard drawing for popup
     *
     * @param shaderProgram shader program id
     * @param vPMatrix matrix for drawing
     */
    override fun draw(shaderProgram: Int, vPMatrix: FloatArray) {
        if(currentDuration > 0){
            currentDuration--
            if (currentDuration <1) {
                popupOn = false; popupCallback?.let { it() }
            }
        }
        if(popupOn) {
            framesOn++
            super.draw(shaderProgram, vPMatrix)
            popupTextBox.draw(shaderProgram, vPMatrix)
            answerButton.draw(shaderProgram, vPMatrix)
        }
    }

    /**
     * Shows current popup setting up button or timer for popup hinding
     */
    fun showPopup() {
        popupOn = true
        if( duration > 0) {
            currentDuration = duration; return
        }

        answerButton.onClickAction {
            println("Click detected")
            popupOn = false;
            framesOn = 0
            popupCallback?.let { it() };
            answerButton.removeClickAction()
        }

    }

    /**
     * Hides popup immediately
     *
     */
    fun hidePopup(){
        popupOn = false
        framesOn = 0
    }

    override fun click(x: Float, y: Float): Boolean {
        if (!popupOn || framesOn < 30 ) return false

        return answerButton.click(x, y)
    }
    private fun updatePopupText(){
        popupTextBox.displayedText = popupText
    }

    private fun updatePopupAnwser(){
        answerButton.displayedText = popupAnswer
    }
}
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
 * @param id identification for one popup for using in
 *
 * @author Adam Czyżak
 */
class PopupDouble(background: Bitmap, popupText: String, popupAnswer1: String = "", popupAnswer2: String = "", val duration: Int = -1, id: String = "") : GameObject(bitmap = background , id= id) {
    private val timedPopup: Boolean
    private var popupOn: Boolean = false
    private var currentDuration = duration
    private var popupCallback1: (() -> Unit)? = null
    private var popupCallback2: (() -> Unit)? = null
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
     * First text on the bottom of popup - can be clicked to dismiss a popup
     */
    var popupAnswer1: String = popupAnswer1
        get() = field
        set(value){
            field = value
        }
    /**
     * Second text on the bottom of popup - can be clicked to dismiss a popup
     */
    var popupAnswer2: String = popupAnswer2
        get() = field
        set(value){
            field = value
        }

    var popupTextBox: TextBox
    var answerButton1: TextBox
    var answerButton2: TextBox

    /**
     * Sets function that will be called after popup disappears
     *
     * @param func Function to be called after popup disappears
     */
    fun setPopupCallback1(func: ()->Unit){
        popupCallback1 = func
    }

    fun setPopupCallback2(func: ()->Unit){
        popupCallback2 = func
    }
    init{
        if(duration > 0) timedPopup = true
        else timedPopup = false

        this.scale(0.75f)

        popupTextBox = TextBox(id = "${id} - Textbox",  size = 32f, width = 300, initialText = popupText)
        popupTextBox.setOriginPosition(y = 0.1f)
        popupTextBox.scale(0.5f)

        answerButton1 = TextBox(id = "${id} - Answer", size = 24f, initialText = popupAnswer1,
            color = Color.GREEN, height = 100, width = 300)
        answerButton1.setOriginPosition(y = -0.15f)
        answerButton1.scale(0.25f)

        answerButton2 = TextBox(id = "${id} - Answer", size = 32f, initialText = popupAnswer2,
            color = Color.RED, height = 100, width = 300)
        answerButton2.setOriginPosition(y = -0.3f)
        answerButton2.scale(0.25f)
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
                popupOn = false; popupCallback1?.let { it() }
            }
        }
        if(popupOn) {
            framesOn++
            super.draw(shaderProgram, vPMatrix)
            popupTextBox.draw(shaderProgram, vPMatrix)
            answerButton1.draw(shaderProgram, vPMatrix)
            answerButton2.draw(shaderProgram, vPMatrix)
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

        answerButton1.onClickAction {
            println("Click detected")
            popupOn = false
            framesOn = 0
            popupCallback1?.let { it() }
            answerButton1.removeClickAction()
        }

        answerButton2.onClickAction {
            println("Click detected")
            popupOn = false
            framesOn = 0
            popupCallback2?.let { it() }
            answerButton2.removeClickAction()
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

        return answerButton1.click(x, y) || answerButton2.click(x, y)
    }
    private fun updatePopupText(){
        popupTextBox.displayedText = popupText
    }

    private fun updatePopupAnwser1(){
        answerButton1.displayedText = popupAnswer1
    }

    private fun updatePopupAnwser2(){
        answerButton2.displayedText = popupAnswer2
    }

    override fun refresh() {
        super.refresh()
        answerButton1.refresh()
        answerButton2.refresh()
        popupTextBox.refresh()
    }
}
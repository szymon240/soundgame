package pl.soundgame.engine.gameobjects

import android.graphics.Bitmap
import pl.soundgame.engine.shapes.Color

class Popup(background: Bitmap, popupText: String, popupAnswer: String = "", val duration: Int = -1, id: String = "") : GameObject(bitmap = background , id= id) {
    val timedPopup: Boolean
    var popupOn: Boolean = false
    var currentDuration = duration
    private var popupCallback: (() -> Unit)? = null
    var framesOn: Int = 0
    var popupText: String = popupText
        get() = field
        set(value){
            field = value
        }
    var popupAnswer: String = popupAnswer
        get() = field
        set(value){
            field = value
        }

    public var popupTextBox: TextBox
    private var answerButton: TextBox

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
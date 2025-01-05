package pl.soundgame.engine.gameobjects

import android.content.Context
import android.graphics.Bitmap
import pl.soundgame.R
import pl.soundgame.engine.Game
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.engine.shapes.Color
import pl.soundgame.playerutils.Achievement

class PopupAchievement(background: Bitmap, achievement: Achievement, context: Context, popupAnswer: String = "", val duration: Int = -1, id: String = "") : GameObject(bitmap = background , id= id) {
    private val timedPopup: Boolean
    private var popupOn: Boolean = false
    private var currentDuration = duration
    private var popupCallback: (() -> Unit)? = null
    private var framesOn: Int = 0


    /**
     * Achievement unlcok to display
     */
    var achievement: Achievement = achievement


    /**
     * Text on the bottom of popup - can be clicked to dismiss a popup
     */
    var popupAnswer: String = popupAnswer

    var popupTextBox: TextBox
    var popupTextBoxDescription: TextBox
    var answerButton: TextBox
    var image: GameObject

    /**
     * Sets function that will be called after popup disappears
     *
     * @param func Function to be called after popup disappears
     */
    fun setPopupCallback(func: ()->Unit){
        popupCallback = func
    }
    init{
        timedPopup = duration > 0

        this.scale(0.75f)

        popupTextBox = TextBox(id = "${id} - Textbox",  size = 32f, width = 300, initialText = achievement.name)
        popupTextBox.setOriginPosition(y = 0.3f)
        popupTextBox.scale(0.5f)

        popupTextBoxDescription = TextBox(id = "${id} - Textbox",  size = 20f, width = 300, initialText = achievement.description)
        popupTextBoxDescription.setOriginPosition(y = -0.2f)
        popupTextBoxDescription.scale(0.5f)

        answerButton = TextBox(id = "${id} - Answer", size = 32f, initialText = context.getString(R.string.rhythm_popup_answser), color = Color.GREEN)
        answerButton.setOriginPosition(y = -0.3f)
        answerButton.scale(0.5f)

        image = GameObject(id = "${id} - image", bitmap = loadTextureBitmap(achievement.textureName, context))
        image.setOriginPosition(y = 0.0f)
        image.scale(0.35f)
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
            popupTextBoxDescription.draw(shaderProgram, vPMatrix)
            image.draw(shaderProgram, vPMatrix)
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
            popupOn = false
            framesOn = 0
            popupCallback?.let { it() }
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

    private fun updatePopupAnwser(){
        answerButton.displayedText = popupAnswer
    }

    override fun refresh() {
        super.refresh()
        answerButton.refresh()
        popupTextBox.refresh()
        popupTextBoxDescription.refresh()
        image.refresh()
    }
}
package pl.soundgame.modes

import android.content.Context
import pl.soundgame.SoundPlayer
import pl.soundgame.connection.serializedclasses.Question
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap

class InstrumentalMode(
    var context: Context,
    private val changeModeCallback: (GameModeName) -> Unit,
    private var questions: List<Question>, // List of questions passed into the mode
    private val totalRounds: Int
) : GameMode() {

    private val soundPlayer: SoundPlayer = SoundPlayer(context)
    private var currentRound = 0
    private var score = 0

    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            setupScene(scene)
        }
        return scene
    }

    private fun setupScene(scene: Scene) {
        if (currentRound >= totalRounds) {
            // If the game is over, display the score and exit
            println("Game Over! Your score: $score")
            return
        }

        // Get the current question from the list based on the current round
        val currentQuestion = questions.getOrNull(currentRound)
        if (currentQuestion == null) {
            println("No more questions available!")
            return
        }

        println("Displaying Question: ${currentQuestion.question}")

        // Create button to play the music/sound
        val playMusicButton = Button(loadTextureBitmap("button.png", context), id = "playMusicButton")
        playMusicButton.setOriginPosition(x = 0.0f, y = 0.4f)
        playMusicButton.scale(0.25f)
        playMusicButton.onClickAction { soundPlayer.playFromUrl(currentQuestion.url) }
        scene.addGameObject(playMusicButton)

        // Create answer buttons for the question
        createAnswerButton(scene, currentQuestion.ans1 ?: "", x = -0.5f, y = -0.2f)
        createAnswerButton(scene, currentQuestion.ans2 ?: "", x = 0.5f, y = -0.2f)
        createAnswerButton(scene, currentQuestion.ans3 ?: "", x = -0.5f, y = -0.6f)
        createAnswerButton(scene, currentQuestion.ans4 ?: "", x = 0.5f, y = -0.6f)

        // Exit button
        val exitButton = Button(loadTextureBitmap("button.png", context), id = "exitButton")
        exitButton.setOriginPosition(y = 0.85f, x = -0.65f)
        exitButton.scale(0.2f)
        exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
        scene.addGameObject(exitButton)
    }

    private fun createAnswerButton(scene: Scene, text: String, x: Float, y: Float) {
        val button = Button(loadTextureBitmap("button.png", context), id = "answerButton-$text")
        button.setOriginPosition(x = x, y = y)
        button.scale(0.25f)
        button.onClickAction { checkAnswer(text) }
        scene.addGameObject(button)

        val textBox = TextBox(initialText = text, id = "textBox-$text")
        textBox.setOriginPosition(x = x, y = y)
        textBox.scale(0.2f)
        scene.addGameObject(textBox)
    }

    private fun checkAnswer(selectedAnswer: String) {
        // Get the current question for this round
        val currentQuestion = questions.getOrNull(currentRound)
        if (currentQuestion != null) {
            // Check if the selected answer is correct
            if (selectedAnswer == getCorrectAnswer(currentQuestion)) {
                println("Correct! The answer was $selectedAnswer.")
                score++
            } else {
                println("Wrong! The correct answer was ${getCorrectAnswer(currentQuestion)}.")
            }
            // Move to the next round and refresh the question
            currentRound++
            if (currentRound < totalRounds) {
                refreshQuestion()
            } else {
                // End of game, show the final score and transition to the menu
                println("Game Over! Your final score: $score")
                changeModeCallback(GameModeName.MENU) // Go back to menu
            }
        }
    }

    private fun refreshQuestion() {
        // Create and load a new scene with the next question
        val scene = returnGameModeScene()
        scene.loadScene()
    }

    private fun getCorrectAnswer(question: Question): String? {
        return when (question.correctAnswer) {
            1 -> question.ans1 ?: ""
            2 -> question.ans2 ?: ""
            3 -> question.ans3 ?: ""
            4 -> question.ans4 ?: ""
            else -> ""
        }
    }
}

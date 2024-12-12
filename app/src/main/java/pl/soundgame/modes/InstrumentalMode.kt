package pl.soundgame.modes

import android.content.Context
import android.util.Log
import androidx.appcompat.view.menu.ActionMenuItemView.PopupCallback
import pl.soundgame.R
import pl.soundgame.SoundPlayer
import pl.soundgame.connection.serializedclasses.Question
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.Popup
import pl.soundgame.engine.gameobjects.PopupDouble
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.engine.shapes.createTextTexture
import java.io.File

/**
 * InstrumentalMode - a game mode where players answer questions based on audio cues.
 * This mode uses audio playback and user interaction through a series of buttons
 * representing possible answers.
 *
 * @property context Android context for loading resources and interacting with the system.
 * @property changeModeCallback Callback for switching to a different game mode.
 * @property questions List of questions used in the game, each containing answers and a sound URL.
 * @property totalRounds Total number of rounds for the game session.
 *
 * @author Szymon Szymankiewicz
 */
class InstrumentalMode(
    var context: Context,
    private val changeModeCallback: (GameModeName) -> Unit,
    private var questions: List<Question>,
    private val totalRounds: Int,
    private val onCompleteCallback: (Double) -> Unit
) : GameMode() {
    private val soundPlayer: SoundPlayer = SoundPlayer(context)
    private var currentRound = 0
    private var score = 0
    private var lastScore = 0
    private val TAG = "Instrumental Mode"

    /**
     * Creates and returns the game mode's scene.
     *
     * @return Scene containing all UI elements and gameplay logic for the instrumental mode.
     */
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

    private val scoreExampleText = context.getString(R.string.score_example_text)
    private val roundExampleText = context.getString(R.string.round_example_text)
    private val finalScoreText = context.getString(R.string.final_score_text)
    private val finishGameText = context.getString(R.string.finish_game_text)

    /**
     * Sets up the scene by adding buttons, text boxes, and the music playback feature.
     *
     * @param scene The scene object to which game objects will be added.
     */
    private fun setupScene(scene: Scene) {
        val scoreText = TextBox(initialText = "$scoreExampleText $score", id = "scoreText")
        val roundText = TextBox(initialText = "$roundExampleText $currentRound", id = "roundText")

        val popup = Popup(
            loadTextureBitmap("popupBackgound.png", context),
            context.getString(R.string.tutorial_instrumental),
            popupAnswer = context.getString(R.string.tutorial_rhythm_answer)
        )

        val exitPopup = PopupDouble(
            loadTextureBitmap("popupBackgound.png", context),
            context.getString(R.string.exit_popup_text),
            popupAnswer1 = context.getString(R.string.exit_no),
            popupAnswer2 = context.getString(R.string.exit_yes)
        )

        lateinit var ans1: Button
        lateinit var ans2: Button
        lateinit var ans3: Button
        lateinit var ans4: Button
        var currentURL = "";
        val exitButton = Button(loadTextureBitmap("back.png", context), id = "exitButton")
        val playMusicButton = Button(loadTextureBitmap("rhythm_mode/play.png", context), id = "playMusicButton")

        /**
         * Loads the questions for the Instrumental mode.
         * This could involve processing or validating the questions.
         */
        fun refreshQuestion() {
            var q = questions[currentRound]
            ans1.changeBaseBitmap(createTextTexture(text = "${q.ans1}", size = 90f, background = loadTextureBitmap("button.png", context)))
            ans2.changeBaseBitmap(createTextTexture(text = "${q.ans2}", size = 90f, background = loadTextureBitmap("button.png", context)))
            ans3.changeBaseBitmap(createTextTexture(text = "${q.ans3}", size = 90f, background = loadTextureBitmap("button.png", context)))
            ans4.changeBaseBitmap(createTextTexture(text = "${q.ans4}", size = 90f, background = loadTextureBitmap("button.png", context)))
            currentURL = q.url
            playMusicButton.onClickAction {
                Log.i(TAG, "${currentURL}" );
                val soundFile = questions[currentRound]?.url?.let { context.cacheDir.resolve(it.substringAfterLast("/")) }
                if (soundFile?.exists() == true) {
                    soundPlayer.playSoundWithPitch(1.0f, soundFile.absolutePath)
                }
            }
        }

        /**
         * Retrieves the correct answer for the given question.
         *
         * @param question The question object from which the correct answer is retrieved.
         * @return The correct answer as a string.
         */
        fun getCorrectAnswer(question: Question): String? {
            return when (question.correctAnswer) {
                1 -> question.ans1 ?: ""
                2 -> question.ans2 ?: ""
                3 -> question.ans3 ?: ""
                4 -> question.ans4 ?: ""
                else -> ""
            }
        }

        /**
         * Checks if the player's answer matches the correct answer for the question.
         *
         * @param question The question object containing the correct answer.
         * @param playerAnswer The answer provided by the player.
         * @return True if the player's answer is correct, false otherwise.
         */
        fun checkAnswer(selectedAnswer: Int) {
            soundPlayer.stopAllSounds()

            val currentQuestion = questions.getOrNull(currentRound)
            if (currentQuestion != null) {
                if (selectedAnswer == currentQuestion.correctAnswer) {
                    score++
                    lastScore = 1
                }
                else {
                    lastScore = 0
                }

                playMusicButton.lock(); exitButton.lock()
                ans1.lock(); ans2.lock(); ans3.lock(); ans4.lock()


                currentRound++
                if (currentRound < totalRounds) {
                    popup.answerButton.displayedText = context.getString(R.string.next_instrumental)
                    roundText.displayedText = "$roundExampleText ${currentRound + 1}"
                    scoreText.displayedText = "$scoreExampleText ${score * 100}"
                    val text = if (lastScore == 1 )
                        context.getString(R.string.correct_instrumental)
                    else
                        "${context.getString(R.string.incorrect_instrumental)} ${getCorrectAnswer(currentQuestion)}"
                    popup.popupTextBox.displayedText = "$text  $scoreExampleText ${score * 100}/${totalRounds * 100}"
                    popup.showPopup()
                    refreshQuestion()
                } else {
                    scoreText.displayedText = "$scoreExampleText ${score * 100}"
                    sendScore()
                    popup.answerButton.displayedText = context.getString(R.string.last_instrumental)
                    val text = if (lastScore == 1 )
                        context.getString(R.string.correct_instrumental)
                    else
                        "${context.getString(R.string.incorrect_instrumental)} ${getCorrectAnswer(currentQuestion)}"
                    popup.popupTextBox.displayedText = "$text $scoreExampleText  ${score * 100}/${totalRounds * 100}"
                    popup.setPopupCallback { changeModeCallback(GameModeName.MENU) }
                    popup.showPopup()

                    println("Game Over! Your final score: $score")
                }
            }
        }

        /**
         * Creates a clickable button for a given answer option.
         *
         * @param context The application context for creating the button.
         * @param answerText The text to display on the button.
         * @param onClickAction The action to perform when the button is clicked.
         * @return A Button object configured with the given parameters.
         */
        fun createAnswerButton(scene: Scene, answer: Int, x: Float, y: Float): Button {
            val button = Button(loadTextureBitmap("button.png", context), id = "answerButton-$answer")
            button.setOriginPosition(x = x, y = y)
            button.scale(0.25f)
            button.onClickAction { checkAnswer(answer) }
            return button
        }

        playMusicButton.setOriginPosition(y = 0.2f, x = 0.0f)
        playMusicButton.scale(0.25f)

        roundText.setOriginPosition(y = 0.8f, x = 0f)
        roundText.scale(0.5f)
        scoreText.setOriginPosition(y = 0.7f, x = 0f)
        scoreText.scale(0.5f)
        scene.addGameObject(scoreText, roundText)

        ans1 = createAnswerButton(scene, 1, x = -0.5f, y = -0.2f)
        ans2 = createAnswerButton(scene, 2, x = 0.5f, y = -0.2f)
        ans3 = createAnswerButton(scene, 3, x = -0.5f, y = -0.6f)
        ans4 = createAnswerButton(scene, 4, x = 0.5f, y = -0.6f)

        exitPopup.setPopupCallback1 {
            exitPopup.hidePopup()
            playMusicButton.unlock(); exitButton.unlock()
            ans1.unlock(); ans2.unlock(); ans3.unlock(); ans4.unlock()
        }

        exitPopup.setPopupCallback2 {
            soundPlayer.stopAllSounds()
            changeModeCallback(GameModeName.MENU)
        }


        exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
        exitButton.scale(0.15f)
        exitButton.onClickAction {
            playMusicButton.lock(); exitButton.lock()
            ans1.lock(); ans2.lock(); ans3.lock(); ans4.lock()
            exitPopup.showPopup()
        }

        refreshQuestion()

        popup.setPopupCallback {
            playMusicButton.unlock(); exitButton.unlock()
            ans1.unlock(); ans2.unlock(); ans3.unlock(); ans4.unlock()
        }
        popup.popupTextBox.size = 26f

        playMusicButton.lock(); exitButton.lock()
        ans1.lock(); ans2.lock(); ans3.lock(); ans4.lock()
        popup.showPopup()

        scene.addGameObject(playMusicButton, exitButton, ans1, ans2, ans3, ans4, popup, exitPopup)
    }

    private fun sendScore() {
        onCompleteCallback(score.toDouble() * 100)
    }
}

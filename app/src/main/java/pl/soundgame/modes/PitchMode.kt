package pl.soundgame.modes

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import pl.soundgame.playerutils.UserManager
import kotlin.random.Random

class PitchMode(
    private val context: Context,
    private val changeModeCallback: (GameModeName) -> Unit,
    private var questions: List<Question>,
    private val onCompleteCallback: (Double) -> Unit
) : GameMode() {
    private val soundPlayer = SoundPlayer(context)
    private var score = 0
    private val TAG = "PitchMode"
    private var currentPitch = 1.0f
    private var pitchRange = 0.5f
    private val minPitchRange = 0.04f
    private val rangeDecrement = 0.02f
    private val userManager: UserManager = UserManager.getInstance(context)

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
        val scoreText = TextBox(initialText = "Score: $score", id = "scoreText")
        val popup = Popup(
            loadTextureBitmap("popupBackground.png", context),
            context.getString(R.string.tutorial_pitch),
            popupAnswer = context.getString(R.string.tutorial_rhythm_answer)
        )

        val exitPopup = PopupDouble(
            loadTextureBitmap("popupBackground.png", context),
            context.getString(R.string.exit_popup_text),
            popupAnswer1 = context.getString(R.string.exit_no),
            popupAnswer2 = context.getString(R.string.exit_yes)
        )

        val higherButton = Button(loadTextureBitmap("higher.png", context), id = "higherButton")
        val lowerButton = Button(loadTextureBitmap("lower.png", context), id = "lowerButton")
        val playButton = Button(loadTextureBitmap("rhythm_mode/play.png", context), id = "playButton")
        val exitButton = Button(loadTextureBitmap("back.png", context), id = "exitButton")

        val higherButtonText = TextBox(initialText = context.getString(R.string.higherButtonText), id = "higherButtonText")
        higherButtonText.setOriginPosition(y = -0.2f, x = 0.5f)
        higherButtonText.scale(0.4f)

        val lowerButtonText = TextBox(initialText = context.getString(R.string.lowerButtonText), id = "lowerButtonText")
        lowerButtonText.setOriginPosition(y = -0.2f, x = -0.5f)
        lowerButtonText.scale(0.4f)

        higherButton.setOriginPosition(x = 0.5f, y = -0.5f)
        lowerButton.setOriginPosition(x = -0.5f, y = -0.5f)
        playButton.setOriginPosition(x = 0f, y = 0.3f)
        scoreText.setOriginPosition(x = 0f, y = 0.8f)

        higherButton.scale(0.4f)
        lowerButton.scale(0.4f)
        playButton.scale(0.4f)
        scoreText.scale(0.5f)

        exitPopup.setPopupCallback1 {
            exitPopup.hidePopup()
            exitButton.unlock()
        }

        exitPopup.setPopupCallback2 {
            soundPlayer.stopAllSounds()
            changeModeCallback(GameModeName.MENU)
        }

        exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
        exitButton.scale(0.15f)
        exitButton.onClickAction {
            exitButton.lock()
            exitPopup.showPopup()
        }

        scene.addGameObject(scoreText, higherButtonText, lowerButtonText, higherButton, lowerButton, playButton, popup, exitPopup, exitButton)

        var nextPitch = generateNewPitch()

        fun playCurrentAndNextSounds() {
            val soundFile = questions[0].url.let { context.cacheDir.resolve(it.substringAfterLast("/")) }
            if (soundFile.exists()) {
                Log.d(TAG, "Playing sound from file: ${soundFile.absolutePath}")
                soundPlayer.playSoundWithPitch(currentPitch, soundFile.absolutePath)

                Handler(Looper.getMainLooper()).postDelayed({
                    Log.d(TAG, "Playing next sound with pitch: $nextPitch")
                    soundPlayer.playSoundWithPitch(nextPitch, soundFile.absolutePath)
                }, 1500)
            } else {
                Log.e(TAG, "Sound file does not exist: ${soundFile.absolutePath}")
            }

            Log.d(TAG, "currentPitch: $currentPitch, nextPitch: $nextPitch")
        }


        fun checkAnswer(isHigher: Boolean) {
            println("SPRAWDZAM")
            val isCorrect = (isHigher && nextPitch > currentPitch) || (!isHigher && nextPitch < currentPitch)

            if (isCorrect) {
                score+= 100
                scoreText.displayedText = "Score: $score"
                currentPitch = nextPitch
                nextPitch = generateNewPitch()
                pitchRange = (pitchRange - rangeDecrement).coerceAtLeast(minPitchRange)
            } else {
                endGame(scene, popup)
            }
        }

        higherButton.onClickAction { checkAnswer(isHigher = true) }
        lowerButton.onClickAction { checkAnswer(isHigher = false) }
        playButton.onClickAction { println("KLIK")
            playCurrentAndNextSounds() }

        popup.setPopupCallback {
            playButton.unlock()
            higherButton.unlock()
            lowerButton.unlock()
        }

        popup.popupTextBox.size = 26f
        popup.showPopup()
        playButton.lock()
        higherButton.lock()
        lowerButton.lock()
    }

    private fun generateNewPitch(): Float {
        Log.d(TAG, "Generating new pitch")
        return currentPitch + Random.nextFloat() * pitchRange * 2 - pitchRange
    }

    private fun endGame(scene: Scene, popup: Popup) {
        soundPlayer.stopAllSounds()
        popup.popupTextBox.displayedText = "Game Over! Final Score: $score"
        popup.setPopupCallback { changeModeCallback(GameModeName.MENU) }
        popup.showPopup()

        // Send score to callbacks and update achievements
        val finalScore = score.toDouble()
        Log.i(TAG, "Final score: $finalScore")

        // Update game stats
        Log.i(TAG, "Updating game stat: gamesPlayed")
        userManager.incrementGamesPlayed()

        Log.i(TAG, "Updating high score for rhythm with score: $finalScore")
        userManager.updateHighScore("pitch", finalScore)

        Log.i(TAG, "Incrementing total score by: $finalScore")
        userManager.incrementTotalScore(finalScore)

        onCompleteCallback(finalScore)
    }
}

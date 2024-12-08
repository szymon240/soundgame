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
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import kotlin.random.Random


/**
 * RhythmMode Class
 *
 * Implements a rhythm-based game mode where players replicate rhythm patterns.
 * Features include dynamic sound generation, user input tracking, accuracy measurement,
 * and gameplay progression through multiple rounds.
 *
 * @param context - Android context for accessing resources and system services
 * @param changeModeCallback - Callback to switch between game modes
 * @param questions - List of questions with associated sound URLs
 * @param totalRounds - Number of game rounds
 *
 * @author Szymon Szymankiewicz
 */
class RhythmMode(
    var context: Context,
    private val changeModeCallback: (GameModeName) -> Unit,
    private val questions: List<Question>,
    private val totalRounds: Int,
    private val onCompleteCallback: (Double) -> Unit
) : GameMode() {

    private val soundPlayer: SoundPlayer = SoundPlayer(context)
    private val handler = Handler(Looper.getMainLooper())
    private val rhythmPattern = mutableListOf<Pair<Boolean, Float>>()
    private val rhythmIntervals = mutableListOf<Long>()
    private val userPressIntervals = mutableListOf<Long>()
    private var tempoBPM = 60
    private var isFirst = true
    private var start = true
    private var unblocked = false
    private var startTime = 0L
    private var lastPressTime = 0L
    private var accuracy = 0.0
    private var roundNumber = 0
    private val TAG = "RHYTHM MODE"
    private var lastRoundScore = 0.0f
    private var playingPattern = false

    /**
     * Creates and initializes the game scene.
     * Sets up buttons, text, and event handling for gameplay.
     */
    override fun returnGameModeScene(): Scene {
        Log.i(TAG, "Creating scene")
        logAllQuestions()

        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        val popup = Popup(
            loadTextureBitmap("popupBackgound.png", context),
            context.getString(R.string.tutorial_rhythm),
            popupAnswer = context.getString(R.string.tutorial_rhythm_answer)
        )

        val scoreExampleText = context.getString(R.string.score_example_text)
        val roundExampleText = context.getString(R.string.round_example_text)
        val finalScoreText = context.getString(R.string.final_score_text)
        val finishGameText = context.getString(R.string.finish_game_text)

        // Configure scene elements
        scene.setInitScene {
            // Add buttons, text boxes, and event listeners
            val scoreText = TextBox(initialText = "$scoreExampleText $accuracy", id = "scoreText")
            val playButton = Button(loadTextureBitmap("rhythm_mode/play.png", context), id = "playButton")
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "exitButton")
            val tapButton = Button(
                loadTextureBitmap("rhythm_mode/roundbutton_off.png", context),
                id = "tapButton",
                alternateBitmap = loadTextureBitmap("rhythm_mode/roundbutton_on.png", context)
            )
            val roundText = TextBox(initialText = "$roundExampleText ${roundNumber + 1}", id = "roundText")

            // Set positions and scaling for UI elements
            roundText.setOriginPosition(y = 0.8f, x = 0f)
            roundText.scale(0.5f)
            scoreText.setOriginPosition(y = 0.5f, x = 0f)
            scoreText.scale(0.5f)
            scene.addGameObject(scoreText, roundText)
            tapButton.setOriginPosition(y = -0.5f, x = 0f)
            tapButton.scale(0.5f)

            var currentPatternIndex = 0

            // Add interaction logic to buttons
            tapButton.onClickAction {
                if (unblocked && !playingPattern) {
                    startTime = System.currentTimeMillis()
                    val pitch = if (currentPatternIndex < rhythmPattern.size) rhythmPattern[currentPatternIndex].second else 1.0f
                    currentPatternIndex++

                    val questionUrl = questions.getOrNull(roundNumber)?.url

                    if (!questionUrl.isNullOrEmpty()) {
                        soundPlayer.playSoundWithPitch(pitch, questionUrl)
                    } else {
                        val beatSound = soundPlayer.getSoundById(4)
                        beatSound?.let {
                            soundPlayer.playSoundWithPitch(pitch, it.resId)
                        }
                    }

                    val pressTime = System.currentTimeMillis()
                    if (isFirst) {
                        userPressIntervals.add(0)
                        isFirst = false
                        lastPressTime = System.currentTimeMillis()
                    } else {
                        userPressIntervals.add(pressTime - lastPressTime)
                    }
                    lastPressTime = pressTime

                    if (userPressIntervals.size == rhythmIntervals.size) {
                        checkAccuracy()
                        isFirst = true
                        unblocked = false
                        currentPatternIndex = 0
                        userPressIntervals.clear()
                        if (roundNumber < totalRounds) {
                            roundNumber++
                            if (roundNumber >= totalRounds) {
                                sendScore()
                            }
                            scoreText.displayedText = "$scoreExampleText ${"%.2f".format(accuracy)}"
                            roundText.displayedText = "$roundExampleText $roundNumber"
                            popup.popupTextBox.size = 32f
                            popup.popupTextBox.displayedText = "$roundExampleText ${roundNumber}\n $scoreExampleText ${"%.2f".format(lastRoundScore)}/100"
                            playButton.lock()
                            exitButton.lock()
                            tapButton.lock()
                            popup.showPopup()
                        } else {
                            scoreText.displayedText = "$finalScoreText ${"%.2f".format(accuracy)}"
                            roundText.displayedText = "$finishGameText"
                            popup.setPopupCallback { changeModeCallback(GameModeName.MENU) }
                            popup.popupTextBox.displayedText = "$roundExampleText ${roundNumber}\n $scoreExampleText ${"%.2f".format(lastRoundScore)}/${totalRounds * 100}"
                            playButton.lock()
                            exitButton.lock()
                            tapButton.lock()
                            popup.showPopup()
                        }
                    }
                }
            }

            scene.addGameObject(tapButton)

            playButton.setOriginPosition(y = -0.1f, x = 0.0f)
            playButton.scale(0.25f)
            playButton.onClickAction {
                // Starts rhythm pattern playback
                if (roundNumber < totalRounds) {
                    generateRhythmPattern()
                    startTime = System.currentTimeMillis()
                    playRhythmPattern()
                    unblocked = true
                    start = false
                } else {
                    scoreText.displayedText = "$finalScoreText ${"%.2f".format(accuracy)}"
                    roundText.displayedText = "$finishGameText"
                    popup.setPopupCallback { changeModeCallback(GameModeName.MENU) }
                    popup.popupTextBox.displayedText = "$finalScoreText ${"%.2f".format(accuracy)}"
                    playButton.lock()
                    exitButton.lock()
                    tapButton.lock()
                    popup.showPopup()
                }
            }
            scene.addGameObject(playButton)

            exitButton.setOriginPosition(y = 0.2f, x = -0.5f)
            exitButton.scale(0.25f)
            exitButton.onClickAction {
                // Exits the game mode
                changeModeCallback(GameModeName.MENU)
            }
            scene.addGameObject(exitButton)

            scene.addGameObject(popup)
            popup.setPopupCallback {
                popup.answerButton.displayedText = context.getString(R.string.rhythm_popup_answser)
                playButton.unlock()
                exitButton.unlock()
                tapButton.unlock()
            }
            popup.popupTextBox.size = 24f
            playButton.lock()
            exitButton.lock()
            tapButton.lock()
            popup.showPopup()
        }
        Log.i(TAG, "Returning scene")
        return scene
    }

    /**
     * Logs all questions with their details.
     */
    private fun logAllQuestions() {
        Log.i(TAG, "Logging all questions with full details:")
        for ((index, question) in questions.withIndex()) {
            Log.i(
                TAG, """
            |Question ${index + 1}:
            |  Question Text: ${question.question}
            |  Correct Answer: ${question.correctAnswer}
            |  Answer 1: ${question.ans1}
            |  Answer 2: ${question.ans2}
            |  Answer 3: ${question.ans3}
            |  Answer 4: ${question.ans4}
            |  URL: ${question.url}
            """.trimMargin()
            )
        }
    }


    /**
     * Generates a random rhythm pattern for the current round.
     */
    fun generateRhythmPattern() {
        rhythmPattern.clear()
        rhythmIntervals.clear()

        val totalBeats = 8

        rhythmPattern.add(Pair(true, Random.nextFloat() * 1.5f + 0.5f))

        repeat(totalBeats - 1) {
            val playSound = Random.nextBoolean()
            val randomPitch = if (playSound) Random.nextFloat() * 1.5f + 0.5f else 1.0f
            rhythmPattern.add(Pair(playSound, randomPitch))
        }

        Log.i(TAG, "Generated Rhythm Pattern: $rhythmPattern")
    }

    /**
     * Plays the generated rhythm pattern and schedules user input.
     */
    fun playRhythmPattern() {
        startTime = System.currentTimeMillis()
        lastPressTime = 0L
        userPressIntervals.clear()

        playingPattern = true

        var isFirst = true
        var delay = 0L
        val beatInterval = (60000L / (tempoBPM * 2))
        val question = questions.getOrNull(roundNumber)
        var lastDelay = 0L

        for ((playSound, pitch) in rhythmPattern) {
            if (playSound) {
                if (isFirst) {
                    rhythmIntervals.add(delay)
                    isFirst = false
                    lastDelay = delay
                } else {
                    rhythmIntervals.add(delay - lastDelay)
                    lastDelay = delay
                }
            }

            handler.postDelayed({
                if (playSound) {
                    val soundFile = question?.url?.let { context.cacheDir.resolve(it.substringAfterLast("/")) }
                    if (soundFile?.exists() == true) {
                        soundPlayer.playSoundWithPitch(pitch, soundFile.absolutePath)
                    }
                    //FIXME if something goes wrong uncomment it pls
//                    else {
//                        val beatSound = soundPlayer.getSoundById(4)
//                        beatSound?.let {
//                            soundPlayer.playSoundWithPitch(pitch, it.resId)
//                        }
//                    }
                }
            }, delay)

            delay += beatInterval
        }

        handler.postDelayed( {
            playingPattern = false
        }, delay)
    }

    /**
     * Compares user input with the rhythm pattern to calculate accuracy.
     */
    private fun checkAccuracy() {
        Log.i(TAG, "Checking accuracy")
        Log.i(TAG, "Generated Intervals: $rhythmIntervals")
        Log.i(TAG, "User Press Intervals: $userPressIntervals")

        var score = 0f
        val comparisonCount = minOf(rhythmIntervals.size, userPressIntervals.size)

        for (i in 1 until comparisonCount) {
            val interval1 = rhythmIntervals[i]
            val interval2 = userPressIntervals[i]
            val maxInterval = maxOf(interval1, interval2)
            val minInterval = minOf(interval1, interval2)

            val ratioScore = (minInterval.toFloat() / maxInterval.toFloat()) * 100
            score += ratioScore
        }

        lastRoundScore = (score / (rhythmIntervals.size - 1))
        accuracy += lastRoundScore
        Log.i(TAG, "Score: ${"%.2f".format(accuracy)}")
    }

    private fun sendScore() {
        onCompleteCallback(accuracy)
    }
}
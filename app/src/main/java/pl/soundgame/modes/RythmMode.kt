package pl.soundgame.modes

import android.content.Context
import pl.soundgame.SoundPlayer
import pl.soundgame.engine.Scene
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.engine.shapes.createTextTexture
import android.os.Handler
import android.os.Looper
import pl.soundgame.engine.background.SampleBackground
import kotlin.math.abs
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.TextBox
import kotlin.random.Random

class RythmMode(var rounds: Int = 8, var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    private val soundPlayer: SoundPlayer = SoundPlayer(context)
    private val handler = Handler(Looper.getMainLooper())
    private val rhythmPattern = mutableListOf<Pair<Boolean, Float>>()
    private val rhythmIntervals = mutableListOf<Long>()
    private val userPressIntervals = mutableListOf<Long>()
    private val tolerance = 100L
    private var tempoBPM = 60
    private var isFirst = true
    private var start = true
    private var unblocked = false
    private var startTime = 0L
    private var lastPressTime = 0L
    private var accuracy = 0.0
    private var roundNumber = 1

    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        var scoreExampleText = "Score: "
        var roundExampleText = "Round: "

        var finalScore = "Your final score: "
        var finishGame = "Game finished!"

        scene.setInitScene {
            val scoreText = TextBox(initialText = "${scoreExampleText}${accuracy}", id = "scoreText")
            scoreText.setOriginPosition(y = 0.5f, x = 0f)
            scoreText.scale(0.5f)
            scene.addGameObject(scoreText)

            val roundText = TextBox(initialText = "${roundExampleText}${roundNumber}", id = "roundText")
            roundText.setOriginPosition(y = 0.8f, x = 0f)
            roundText.scale(0.5f)
            scene.addGameObject(roundText)


            // Button for the player to press in sync with the rhythm pattern
            val tapButton = Button(loadTextureBitmap("roundbutton_off.png", context), id = "tapButton",
                alternateBitmap = loadTextureBitmap("roundbutton_on.png", context))
            tapButton.setOriginPosition(y = -0.5f, x = 0f)
            tapButton.scale(0.5f)
            tapButton.onClickAction {
                if (unblocked) {
                    if (start) {
                        userPressIntervals.clear()
                        lastPressTime = 0L
                        startTime = System.currentTimeMillis()
                        isFirst = true
                        start = false
                    } else {
                        val beatSound = soundPlayer.getSoundById(4)  // Assuming the beat sound is stored at ID 4
                        beatSound?.let {
                            soundPlayer.setSound(it.resId)
                            soundPlayer.playSoundWithPitch(1.0f)  // You can modify pitch if needed
                        }
                        val pressTime = System.currentTimeMillis()
                        if (isFirst) {
                            userPressIntervals.add(pressTime - startTime)
                            isFirst = false
                        } else {
                            userPressIntervals.add(pressTime - lastPressTime)
                        }
                        lastPressTime = pressTime

                        // Check if user has completed the required number of intervals
                        if (userPressIntervals.size == rhythmIntervals.size) {
                            checkAccuracy()
                            start = true
                            unblocked = false
                            if (roundNumber < rounds) {
                                roundNumber++
                                scoreText.displayedText = "${scoreExampleText}${accuracy}"
                                roundText.displayedText = "${roundExampleText}${roundNumber}"
                            } else {
                                scoreText.displayedText = "${finalScore}${accuracy}"
                                roundText.displayedText = "${finishGame}"
                            }
                        }
                    }
                }
            }
            scene.addGameObject(tapButton)

            // Button to generate and play the rhythm pattern
            val playButton = Button(loadTextureBitmap("button.png", context), id = "playButton")
            playButton.setOriginPosition(y = 0.2f, x = 0.5f)
            playButton.scale(0.25f)
            playButton.onClickAction {
                if (roundNumber < rounds) {
                    generateRhythmPattern()
                    startTime = System.currentTimeMillis()
                    playRhythmPattern()
                    unblocked = true
                } else {
                    scoreText.displayedText = "${finalScore}${accuracy}"
                    roundText.displayedText = "${finishGame}"
                }
            }
            scene.addGameObject(playButton)

            val exitButton = Button(loadTextureBitmap("button.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.2f, x = -0.5f)
            exitButton.scale(0.25f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
            scene.addGameObject(exitButton)
        }
        return scene
    }

    // Generate a random rhythm pattern with varied note lengths
    fun generateRhythmPattern() {
        rhythmPattern.clear()
        rhythmIntervals.clear()  // Clear previous intervals

        val totalBeats = 8

        repeat(totalBeats) {
            val playSound = Random.nextBoolean()
            val randomPitch = if (playSound) Random.nextFloat() * 1.5f + 0.5f else 1.0f
            rhythmPattern.add(Pair(playSound, randomPitch))
        }
        println("Generated Rhythm Pattern: $rhythmPattern")
    }

    // Play the generated rhythm pattern
    fun playRhythmPattern() {
        startTime = System.currentTimeMillis()  // Set start time for rhythm playback
        lastPressTime = 0L                      // Reset last press time for user input
        userPressIntervals.clear()              // Clear previous press intervals

        var isFirst = true
        var delay = 0L
        val beatInterval = (60000L / (tempoBPM * 2))
        val beatSound = soundPlayer.getSoundById(4)
        var lastDelay = 0L

        for ((playSound, pitch) in rhythmPattern) {
            if (playSound) {
                print("$delay|")
                if (isFirst) {
                    rhythmIntervals.add(delay)
                    isFirst = false
                    lastDelay = delay
                }
                else {
                    rhythmIntervals.add(delay - lastDelay)
                    lastDelay = delay
                }
            }
            handler.postDelayed({
                if (playSound) {
                    beatSound?.let {
                        soundPlayer.setSound(it.resId)
                        soundPlayer.playSoundWithPitch(pitch)
                    }
                }
            }, delay)

            delay += beatInterval
        }
    }


    // Check user accuracy by comparing intervals between presses to generated rhythm intervals
    private fun checkAccuracy() {
        var score = 0f
        val comparisonCount = minOf(rhythmIntervals.size, userPressIntervals.size)

        for (i in 0 until comparisonCount) {
            val interval1 = rhythmIntervals[i]
            val interval2 = userPressIntervals[i]
            val maxInterval = maxOf(interval1, interval2)
            val minInterval = minOf(interval1, interval2)

            val ratioScore = (minInterval.toFloat() / maxInterval.toFloat()) * 100
            score += ratioScore
        }

        accuracy += (score / rhythmIntervals.size)
        accuracy = String.format("%.2f", accuracy).toDouble()
        println("Score: $accuracy")
    }
}

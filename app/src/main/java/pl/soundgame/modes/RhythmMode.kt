package pl.soundgame.modes

import android.content.Context
import pl.soundgame.SoundPlayer
import pl.soundgame.engine.Scene
import pl.soundgame.engine.loadTextureBitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import pl.soundgame.R
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.TextBox
import kotlin.random.Random

class RhythmMode(var rounds: Int = 8, var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
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
    private val TAG = "RHYTM MODE"

    override fun returnGameModeScene(): Scene {
        Log.i(TAG,"Creating scene")
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        val scoreExampleText = context.getString(R.string.score_example_text)
        val roundExampleText = context.getString(R.string.round_example_text)

        val final_score_text = context.getString(R.string.final_score_text)
        val finish_game_text = context.getString(R.string.finish_game_text)

        scene.setInitScene {
            val scoreText = TextBox(initialText = "${scoreExampleText} ${accuracy}", id = "scoreText")
            scoreText.setOriginPosition(y = 0.5f, x = 0f)
            scoreText.scale(0.5f)
            scene.addGameObject(scoreText)

            val roundText = TextBox(initialText = "${roundExampleText} ${roundNumber}", id = "roundText")
            roundText.setOriginPosition(y = 0.8f, x = 0f)
            roundText.scale(0.5f)
            scene.addGameObject(roundText)


            // Button for the player to press in sync with the rhythm pattern
            val tapButton = Button(loadTextureBitmap("roundbutton_off.png", context), id = "tapButton",
                alternateBitmap = loadTextureBitmap("roundbutton_on.png", context))
            tapButton.setOriginPosition(y = -0.5f, x = 0f)
            tapButton.scale(0.5f)
            var currentPatternIndex = 0
            tapButton.onClickAction {
                if (unblocked) {
                        startTime = System.currentTimeMillis()
                        val pitch = if (currentPatternIndex < rhythmPattern.size) rhythmPattern[currentPatternIndex].second else 1.0f
                        currentPatternIndex++

                        val beatSound = soundPlayer.getSoundById(4)
                        beatSound?.let {
                            soundPlayer.setSound(it.resId)
                            soundPlayer.playSoundWithPitch(pitch)
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
                            if (roundNumber < rounds) {
                                roundNumber++
                                scoreText.displayedText = "${scoreExampleText} ${"%.2f".format(accuracy)}"
                                roundText.displayedText = "${roundExampleText} ${roundNumber}"
                            } else {
                                scoreText.displayedText = "${final_score_text} ${"%.2f".format(accuracy)}"
                                roundText.displayedText = "${finish_game_text}"
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
                    start = false
                } else {
                    scoreText.displayedText = "${final_score_text} ${"%.2f".format(accuracy)}"
                    roundText.displayedText = "${finish_game_text}"
                }
            }
            scene.addGameObject(playButton)

            val exitButton = Button(loadTextureBitmap("button.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.2f, x = -0.5f)
            exitButton.scale(0.25f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
            scene.addGameObject(exitButton)
        }
        Log.i(TAG, "Retutning  scene")
        return scene
    }

    // Generate a random rhythm pattern with varied note lengths
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

        println("Generated Rhythm Pattern: $rhythmPattern")
    }


    // Play the generated rhythm pattern
    fun playRhythmPattern() {
        startTime = System.currentTimeMillis()
        lastPressTime = 0L
        userPressIntervals.clear()

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
        print(rhythmIntervals)
        print(userPressIntervals)
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

        accuracy += (score / rhythmIntervals.size-1)
        println("Score: ${"%.2f".format(accuracy)}")
    }
}

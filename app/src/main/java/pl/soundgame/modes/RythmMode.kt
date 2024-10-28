package pl.soundgame.modes

import android.content.Context
import pl.soundgame.SoundPlayer
import pl.soundgame.engine.Scene
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.engine.shapes.createTextTexture
import android.os.Handler
import android.os.Looper
import pl.soundgame.engine.gameobjects.Button
import kotlin.random.Random

class RythmMode(var context: Context) : GameMode() {
    val soundPlayer: SoundPlayer = SoundPlayer(context)
    private val handler = Handler(Looper.getMainLooper())
    private val rhythmPattern = mutableListOf<Pair<Boolean, Float>>()
    private val userPressTimes = mutableListOf<Long>()
    var tempoBPM = 60

    private var startTime = 0L

    override fun returnGameModeScene(): Scene {
        val scene = Scene()

        scene.setInitScene {
            // Button to generate and play the rhythm pattern
            val playButton = Button(loadTextureBitmap("button.png", context), id = "playButton")
            playButton.setOriginPosition(y = 0.2f, x = 0.5f)
            playButton.scale(0.25f)
            playButton.setClickAction {
                generateRhythmPattern()
                startTime = System.currentTimeMillis()
                playRhythmPattern()
            }
            scene.addGameObject(playButton)

            // Button for the player to press in sync with the rhythm pattern
            val tapButton = Button(loadTextureBitmap("button.png", context), id = "tapButton")
            tapButton.setOriginPosition(y = 0.5f, x = 0.5f)
            tapButton.scale(0.25f)
            tapButton.setClickAction {
                val pressTime = System.currentTimeMillis() - startTime
                userPressTimes.add(pressTime)  // Log the relative time of each press
            }
            scene.addGameObject(tapButton)

            val finishButton = Button(createTextTexture("Przycisk 3", background= loadTextureBitmap("button.png", context)), id = "finishButton")
            finishButton.setOriginPosition(y = 0.8f, x = 0.5f)
            finishButton.scale(0.25f)
            finishButton.setClickAction {
                checkAccuracy()
            }
            scene.addGameObject(finishButton)
        }

        return scene
    }

    // Generate a random rhythm pattern with varied note lengths
    fun generateRhythmPattern() {
        rhythmPattern.clear()
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
        var delay = 0L
        val beatInterval = (60000L / (tempoBPM * 2))
        val beatSound = soundPlayer.getSoundById(4)

        for ((playSound, pitch) in rhythmPattern) {
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


    // Check user accuracy by comparing button presses to the generated rhythm
    private fun checkAccuracy() {
        val beatInterval = (60000L / (tempoBPM * 2))  // Eighth note duration
        var score = 0
        val tolerance = beatInterval / 4  // Allowable deviation from exact timing

        // Loop through user presses to find a match within tolerance in rhythmPattern beats
        for (pressTime in userPressTimes) {
            var matched = false
            for (i in rhythmPattern.indices) {
                if (rhythmPattern[i].first) {  // Only compare to "play sound" beats
                    val expectedTime = i * beatInterval

                    // Calculate time difference and check if it’s within tolerance
                    if (kotlin.math.abs(expectedTime - pressTime) <= tolerance) {
                        score++
                        matched = true
                        break  // Exit inner loop after finding a match
                    }
                }
            }
        }

        val totalPlayableBeats = rhythmPattern.count { it.first }
        val accuracyPercentage = if (totalPlayableBeats > 0) {
            (score.toFloat() / totalPlayableBeats) * 100
        } else {
            0f  // Avoid division by zero
        }

        println("Accuracy: $accuracyPercentage%")
    }

}

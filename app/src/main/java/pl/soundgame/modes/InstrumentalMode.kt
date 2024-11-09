package pl.soundgame.modes

import android.content.Context
import pl.soundgame.SoundPlayer
import pl.soundgame.engine.Scene
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.loadTextureBitmap
import android.os.Handler
import android.os.Looper
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import kotlin.random.Random

class InstrumentalMode(var context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    private val soundPlayer: SoundPlayer = SoundPlayer(context)
    private val instruments = listOf("Guitar", "Piano", "Drums", "Violin", "Flute", "Trumpet", "Harp", "Saxophone") // Możliwość dodania większej liczby instrumentów
    private var currentInstrument = ""
    private var answerOptions = listOf<String>()

    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            generateNewQuestion()

            // Przycisk powrotu
            val exitButton = Button(loadTextureBitmap("button.png", context), id = "exitButton")
            exitButton.setOriginPosition(y = 0.85f, x = -0.65f)
            exitButton.scale(0.2f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
            scene.addGameObject(exitButton)

            // Przycisk do puszczania muzyki
            val playMusicButton = Button(loadTextureBitmap("button.png", context), id = "playMusicButton")
            playMusicButton.setOriginPosition(x = 0.0f, y = 0.4f)
            playMusicButton.scale(0.25f)
            playMusicButton.onClickAction { playInstrumentSound(currentInstrument) }
            scene.addGameObject(playMusicButton)

            // Tworzenie przycisków odpowiedzi na podstawie answerOptions
            val answerButton1 = Button(loadTextureBitmap("button.png", context), id = "answerButton1")
            answerButton1.setOriginPosition(x = -0.5f, y = -0.2f)
            answerButton1.scale(0.25f)
            answerButton1.onClickAction { checkAnswer(answerOptions[0]) }
            scene.addGameObject(answerButton1)

            val answerButton2 = Button(loadTextureBitmap("button.png", context), id = "answerButton2")
            answerButton2.setOriginPosition(x = 0.5f, y = -0.2f)
            answerButton2.scale(0.25f)
            answerButton2.onClickAction { checkAnswer(answerOptions[1]) }
            scene.addGameObject(answerButton2)

            val answerButton3 = Button(loadTextureBitmap("button.png", context), id = "answerButton3")
            answerButton3.setOriginPosition(x = -0.5f, y = -0.6f)
            answerButton3.scale(0.25f)
            answerButton3.onClickAction { checkAnswer(answerOptions[2]) }
            scene.addGameObject(answerButton3)

            val answerButton4 = Button(loadTextureBitmap("button.png", context), id = "answerButton4")
            answerButton4.setOriginPosition(x = 0.5f, y = -0.6f)
            answerButton4.scale(0.25f)
            answerButton4.onClickAction { checkAnswer(answerOptions[3]) }
            scene.addGameObject(answerButton4)
        }

        return scene
    }

    private fun playInstrumentSound(instrument: String) {
        val soundId = instruments.indexOf(instrument) + 1 // Założenie: ID odpowiadają indeksowi +1
        val sound = soundPlayer.getSoundById(soundId)
        sound?.let {
            soundPlayer.setSound(it.resId)
            soundPlayer.play()
        }
    }

    private fun checkAnswer(selectedInstrument: String) {
        if (selectedInstrument == currentInstrument) {
            println("Correct! It was $currentInstrument.")
        } else {
            println("Wrong! It was $currentInstrument.")
        }
        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        // Wybieranie poprawnej odpowiedzi
        currentInstrument = instruments.random()

        // Generowanie 3 losowych, różnych od currentInstrument opcji
        val incorrectAnswers = instruments.filter { it != currentInstrument }.shuffled().take(3)

        // Łączenie poprawnej odpowiedzi z trzema niepoprawnymi i mieszanie
        answerOptions = (incorrectAnswers + currentInstrument).shuffled()
    }
}

package pl.soundgame.modes

import android.content.Context
import pl.soundgame.SoundPlayer
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap

class InstrumentalMode(private val context: Context, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    private val soundPlayer: SoundPlayer = SoundPlayer(context)
    private val instruments = listOf("Guitar", "Piano", "Drums", "Violin") // Lista instrumentów
    private var currentInstrument = ""

    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        // Tekst z pytaniem
        val questionText = TextBox(initialText = "Which instrument is playing?", id = "questionText")
        questionText.setOriginPosition(y = 0.7f, x = 0f)
        questionText.scale(0.6f)
        scene.addGameObject(questionText)

        // Przycisk do odtwarzania dźwięku
        val playSoundButton = Button(loadTextureBitmap("button.png", context), id = "playSoundButton")
        playSoundButton.setOriginPosition(y = 0.7f, x = 0.6f)
        playSoundButton.scale(0.2f)
        playSoundButton.onClickAction {
            playInstrumentSound(currentInstrument)
        }
        scene.addGameObject(playSoundButton)

        // Przycisk dla odpowiedzi "Guitar"
        val guitarButton = Button(loadTextureBitmap("button.png", context), id = "guitarButton")
        guitarButton.scale(0.4f)
        guitarButton.setOriginPosition(y = 0.3f, x = -0.4f)
        guitarButton.onClickAction {
            checkAnswer("Guitar")
        }
        scene.addGameObject(guitarButton)

        // Przycisk dla odpowiedzi "Piano"
        val pianoButton = Button(loadTextureBitmap("button.png", context), id = "pianoButton")
        pianoButton.scale(0.4f)
        pianoButton.setOriginPosition(y = 0.3f, x = 0.4f)
        pianoButton.onClickAction {
            checkAnswer("Piano")
        }
        scene.addGameObject(pianoButton)

        // Przycisk dla odpowiedzi "Drums"
        val drumsButton = Button(loadTextureBitmap("button.png", context), id = "drumsButton")
        drumsButton.scale(0.4f)
        drumsButton.setOriginPosition(y = -0.1f, x = -0.4f)
        drumsButton.onClickAction {
            checkAnswer("Drums")
        }
        scene.addGameObject(drumsButton)

        // Przycisk dla odpowiedzi "Violin"
        val violinButton = Button(loadTextureBitmap("button.png", context), id = "violinButton")
        violinButton.scale(0.4f)
        violinButton.setOriginPosition(y = -0.1f, x = 0.4f)
        violinButton.onClickAction {
            checkAnswer("Violin")
        }
        scene.addGameObject(violinButton)

        return scene
    }

    // Funkcja do odtwarzania dźwięku wybranego instrumentu
    private fun playInstrumentSound(instrument: String) {
        val soundId = when (instrument) {
            "Guitar" -> 1
            "Piano" -> 2
            "Drums" -> 3
            "Violin" -> 4
            else -> 0
        }
        val sound = soundPlayer.getSoundById(soundId)
        sound?.let {
            soundPlayer.setSound(it.resId)
            soundPlayer.play()
        }
    }

    // Funkcja do sprawdzania poprawności odpowiedzi
    private fun checkAnswer(selectedInstrument: String) {
        if (selectedInstrument == currentInstrument) {
            println("Correct! It was $currentInstrument.")
        } else {
            println("Wrong! It was $currentInstrument.")
        }
        generateNewQuestion()
    }

    // Generuje nowe pytanie z losowym instrumentem
    private fun generateNewQuestion() {
        currentInstrument = instruments.random()
    }
}

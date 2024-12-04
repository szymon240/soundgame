package pl.soundgame

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.soundgame.connection.CommunicationManager
import pl.soundgame.connection.ConnectionStatus
import pl.soundgame.connection.serializedclasses.Question
import pl.soundgame.engine.Game
import pl.soundgame.engine.Scene
import pl.soundgame.modes.GameMode
import pl.soundgame.modes.GameModeName
import pl.soundgame.modes.InstrumentalMode
import pl.soundgame.modes.Menu
import pl.soundgame.modes.RhythmMode
import pl.soundgame.modes.Settings
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlinx.coroutines.GlobalScope

internal class SoundGame(context: Context) : Game() {
    override var mScene: Scene
    private var gameMode: GameMode
    private var context: Context
    private var gameModeName: GameModeName
    private var TAG = "SoundGame Main Object"
    private var changeModeCallback: (GameModeName) -> Unit = { mode -> changeMode(mode) }
    private var rounds = 3
    private val commManager = CommunicationManager()
    private var questions: List<Question> = emptyList()

    init {
        this.context = context
        gameMode = Menu(this.context, changeModeCallback)
        gameModeName = GameModeName.MENU
        mScene = gameMode.returnGameModeScene()
        changeMode(GameModeName.MENU)

        checkServerStatus()
    }

    private fun checkServerStatus() {
        commManager.getServerStatus { status ->
            if (status != null) {
                Log.i(TAG, "App: ${status.app}, Database: ${status.database}")
                CONNECTION_STATUS = ConnectionStatus.SUCCESS
            } else {
                Log.i(TAG, "Something's wrong")
                CONNECTION_STATUS = ConnectionStatus.FAILED
            }
        }
    }

    private fun fetchQuestionsForMode(mode: GameModeName) {
        commManager.getQuestions(mode, rounds) { response ->
            if (response != null) {
                questions = response.questions ?: emptyList()


                // Launch coroutine to download sounds
                GlobalScope.launch {
                    downloadSoundsForQuestions(questions)
                }
            } else {
                Log.e(TAG, "Failed to fetch questions for mode: $mode")
                questions = emptyList()
            }
        }
    }

    private fun logAllQuestions(questions: List<Question>) {
        Log.i(TAG, "Logging all fetched questions:")
        for ((index, question) in questions.withIndex()) {
            Log.i(
                TAG, """
                |Question ${index}:
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
    private suspend fun downloadSound(urlString: String): File? {
        // Replace "rhythm" with "rhytm" in the URL
        val modifiedUrlString = urlString.replace("rhythm", "rhytm")

        return withContext(Dispatchers.IO) {  // Switch to background thread
            try {
                val url = URL(modifiedUrlString)  // Use the modified URL
                val connection = url.openConnection()
                val inputStream = connection.getInputStream()

                // Extract original file name and extension
                val originalFileName = modifiedUrlString.substringAfterLast("/")
                val soundFile = File(context.cacheDir, originalFileName)

                FileOutputStream(soundFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                Log.i(TAG, "Downloaded sound to: ${soundFile.absolutePath}")
                soundFile
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading sound from URL: $modifiedUrlString", e)
                null
            }
        }
    }


    private fun isValidAudioFile(file: File): Boolean {
        val validExtensions = listOf("mp3", "wav", "ogg")
        return validExtensions.any { file.extension.equals(it, ignoreCase = true) }
    }

    fun changeMode(newMode: GameModeName) {
        Log.i(TAG, "Changing mode to: $newMode")

        if (newMode != GameModeName.MENU) {
            fetchQuestionsForMode(newMode)

            if (questions.isEmpty()) {
                Log.e(TAG, "No questions available for mode: $newMode. Staying in the current mode.")
                return
            }

            // Launch a coroutine to download sounds
            GlobalScope.launch {
                val allSoundsDownloaded = downloadSoundsForQuestions(questions)
                if (!allSoundsDownloaded) {
                    Log.e(TAG, "Not all sounds are downloaded. Staying in the current mode.")
                    return@launch
                }
            }
        } else {
            questions = emptyList()
            Log.i(TAG, "Cleared questions and related data.")
        }

        gameMode = when (newMode) {
            GameModeName.MENU -> Menu(this.context, changeModeCallback)
            GameModeName.RHYTHM -> RhythmMode(this.context, changeModeCallback, questions, rounds)
            GameModeName.INSTRUMENTAL -> InstrumentalMode(this.context, changeModeCallback, questions, rounds)
            GameModeName.SETTINGS -> Settings(this.context, changeModeCallback)
        }

        gameModeName = newMode
        mScene = gameMode.returnGameModeScene()
        mScene.loadScene()
    }

    private suspend fun downloadSoundsForQuestions(questions: List<Question>): Boolean {
        var allDownloaded = true
        for (question in questions) {
            val url = question.url ?: continue
            val soundFile = downloadSound(url)
            if (soundFile == null || !isValidAudioFile(soundFile)) {
                allDownloaded = false
                Log.e(TAG, "Failed or invalid sound file from: $url")
            }
        }
        return allDownloaded
    }

    companion object {
        var CONNECTION_STATUS = ConnectionStatus.CONNECTING
    }
}

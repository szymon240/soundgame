package pl.soundgame

import android.content.Context
import android.os.Looper
import android.provider.Settings.Global
import android.util.Log
import android.widget.Toast
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text
import pl.soundgame.connection.NetworkMonitor
import pl.soundgame.connection.serializedclasses.ScoreRequest
import pl.soundgame.connection.serializedclasses.ScoreResponse
import pl.soundgame.engine.UserManager
import pl.soundgame.engine.gameobjects.Popup
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.modes.Empty

/**
 * SoundGame class extends the Game class and serves as the central controller for the game.
 * It manages the game modes, handles fetching questions, downloading associated sounds,
 * and manages the game scenes for different game modes.
 *
 * @param context The context used for application resources and initialization.
 *
 * @authors Adam Czyżak & Szymon Szymankiewicz
 */
internal class SoundGame(context: Context) : Game() {
    override var mScene: Scene
    private var gameMode: GameMode
    private var context: Context
    private var gameModeName: GameModeName
    private var TAG = "SoundGame Main Object"
    private var changeModeCallback: (GameModeName) -> Unit = { mode -> changeMode(mode) }
    private var rounds = 4
    private val userManager: UserManager = UserManager.getInstance(context)
    private val commManager = CommunicationManager()
    private var questions: List<Question> = emptyList()
    private var score = 0.0
    private val networkMonitor = NetworkMonitor(context)
    init {
        this.context = context
        gameMode = Menu(this.context, changeModeCallback)
        gameModeName = GameModeName.MENU
        mScene = gameMode.returnGameModeScene()
        changeMode(GameModeName.MENU)

        checkServerStatus()
        networkMonitor.registerNetworkCallback { isConnected ->
            CONNECTION_STATUS = ConnectionStatus.CONNECTING
            checkServerStatus()
            if (isConnected) {
                Log.d("NetworkStatus", "Connected to the internet")
            } else {
                Log.d("NetworkStatus", "Disconnected from the internet")
            }
        }
    }

    /**
     * Checks the connection status of the server by requesting its status.
     * It updates the connection status based on the server's response.
     */
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

    /**
     * Fetches questions from the server for the specified game mode and number of rounds.
     * Initiates the sound download for the fetched questions.
     *
     * @param mode The game mode for which questions are to be fetched.
     */
    private fun fetchQuestionsForMode(mode: GameModeName) {
        commManager.getQuestions(mode, rounds) { response ->
            if (response != null) {
                questions = response.questions ?: emptyList()

                GlobalScope.launch {
                    downloadSoundsForQuestions(questions)
                }
            } else {
                Log.e(TAG, "Failed to fetch questions for mode: $mode")
                questions = emptyList()
            }
        }
    }

    /**
     * Downloads a sound file from the provided URL.
     * The sound file is saved to the app's cache directory.
     *
     * @param urlString The URL of the sound file to be downloaded.
     * @return The downloaded sound file, or null if the download failed.
     */
    private suspend fun downloadSound(urlString: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection()
                val inputStream = connection.getInputStream()

                val originalFileName = urlString.substringAfterLast("/")
                val soundFile = File(context.cacheDir, originalFileName)

                FileOutputStream(soundFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                Log.i(TAG, "Downloaded sound to: ${soundFile.absolutePath}")
                soundFile
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading sound from URL: $urlString", e)
                null
            }
        }
    }

    /**
     * Checks if the downloaded file is a valid audio file based on its extension.
     *
     * @param file The file to check.
     * @return True if the file has a valid audio extension, false otherwise.
     */
    private fun isValidAudioFile(file: File): Boolean {
        val validExtensions = listOf("mp3", "wav", "ogg")
        return validExtensions.any { file.extension.equals(it, ignoreCase = true) }
    }

    private fun onRhythmModeComplete(finalAccuracy: Double) {
        score = finalAccuracy
        Log.i(TAG, "Final accuracy after all rounds: $score")

        sendScore(gameModeName, score)
    }

    private fun sendScore(mode: GameModeName, score: Double) {
        val roundedScore = String.format("%.2f", score)
        val formattedScore = roundedScore.replace(",", ".").toDouble()
        val username = userManager.getNickname()?.takeIf { it.isNotBlank() } ?: "player"

        commManager.postScore(mode, username, formattedScore) { response ->
            if (response != null) {
                val status = response.status
                if (status == "ok") {
                    Log.i(TAG, "Score successfully posted to the server!")
                } else {
                    Log.e(TAG, "Failed to post score to the server. Status: $status")
                }
            } else {
                Log.e(TAG, "Failed to post score to the server.")
            }
        }

    }

    /**
     * Changes the current game mode. It initializes the new mode, fetches questions, and
     * downloads the necessary sound files.
     *
     * @param newMode The new game mode to switch to.
     */
    fun changeMode(newMode: GameModeName) {
        Log.i(TAG, "Changing mode to: $newMode")
        var retries = 6  // Number of retries allowed

        fun tryChangeMode() {
            if (newMode != GameModeName.MENU && newMode != GameModeName.SETTINGS) {
                if (CONNECTION_STATUS != ConnectionStatus.SUCCESS){
                    MainScope().launch {
                        val text = context.getString(R.string.msg_no_connection)
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(context, text, duration) // in Activity
                        toast.show()
                    }
                    return
                }

                mScene.lockAllButtons()
                fetchQuestionsForMode(newMode)
                mScene.modifyGameObjectsById("popup_loading") { obj ->
                    if(obj is Popup){
                        obj.showPopup()
                    }
                }
                if (questions.isEmpty()) {
                    Log.e(TAG, "No questions available for mode: $newMode. Retrying...")
                    if (retries > 0) {
                        retries--
                        MainScope().launch {
                            Thread.sleep(1000)  // Wait 1 second before retrying

                            tryChangeMode()
                        }
                    } else {
                        val text = context.getString(R.string.msg_error_downloading)
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(context, text, duration) // in Activity
                        toast.show()
                        Log.e(TAG, "Failed to load mode: $newMode after retries. Staying in current mode.")
                        mScene.unlockAllButtons()
                        mScene.modifyGameObjectsById("popup_loading") { obj ->
                            if(obj is Popup){
                                obj.hidePopup()
                            }
                        }
                    }
                    return
                }

                // Launch a coroutine to download sounds
                MainScope().launch {
                    val allSoundsDownloaded = downloadSoundsForQuestions(questions)
                    if (!allSoundsDownloaded) {
                        Log.e(TAG, "Not all sounds are downloaded. Retrying...")
                        if (retries > 0) {
                            retries--
                                Thread.sleep(1000)  // Wait 1 second before retrying

                            tryChangeMode()
                        } else {
                            val text = context.getString(R.string.msg_error_downloading)
                            val duration = Toast.LENGTH_SHORT
                            val toast = Toast.makeText(context, text, duration) // in Activity
                            toast.show()
                            Log.e(TAG, "Failed to download sounds for mode: $newMode after retries. Staying in current mode.")
                            mScene.unlockAllButtons()
                            mScene.modifyGameObjectsById("popup_loading") { obj ->
                                if(obj is Popup){
                                    obj.hidePopup()
                                }
                            }
                        }
                        return@launch
                    }

                    // Proceed with mode initialization once everything is ready
                    initializeGameMode(newMode)
                }
            } else {
                questions = emptyList()
                Log.i(TAG, "Cleared questions and related data.")
                initializeGameMode(newMode)
            }
        }

        tryChangeMode()
    }

    private fun initializeGameMode(newMode: GameModeName) {
        gameMode = when (newMode) {
            GameModeName.MENU -> Menu(this.context, changeModeCallback)
            GameModeName.RHYTHM -> RhythmMode(this.context, changeModeCallback, questions, rounds, ::onRhythmModeComplete)
            GameModeName.INSTRUMENTAL -> InstrumentalMode(this.context, changeModeCallback, questions, rounds, ::onRhythmModeComplete)
            GameModeName.SETTINGS -> Settings(this.context, changeModeCallback)
            GameModeName.EMPTY -> Empty(this.context, changeModeCallback, GameModeName.MENU)
        }

        gameModeName = newMode
        mScene = gameMode.returnGameModeScene()
        mScene.loadScene()

        GlobalScope.launch {  delay(500) ; withContext(Dispatchers.Main){ } }
    }

    /**
     * Downloads sound files for each question in the list.
     * Returns true if all sounds are downloaded successfully, false if any download fails.
     *
     * @param questions The list of questions for which sounds need to be downloaded.
     * @return True if all sounds are downloaded, false if any download fails.
     */
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

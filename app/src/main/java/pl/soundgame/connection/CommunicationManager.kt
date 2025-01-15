package pl.soundgame.connection

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.soundgame.R
import pl.soundgame.connection.serializedclasses.Question
import pl.soundgame.connection.serializedclasses.Request
import pl.soundgame.connection.serializedclasses.Response
import pl.soundgame.connection.serializedclasses.ScoreRequest
import pl.soundgame.connection.serializedclasses.ScoreResponse
import pl.soundgame.connection.serializedclasses.ScoreTop10Response
import pl.soundgame.connection.serializedclasses.StatusResponse
import pl.soundgame.modes.GameModeName
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CommunicationManager(context: Context) {
    private var parser = Gson()
    private val TAG = "CommunicationManager"
    private val API_KEY = context.getString(R.string.apiKey)

    fun getServerStatus(onResult: (StatusResponse?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val urlToStatus = URL(STATUS_URL)
            try {
                with(urlToStatus.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("x-api-key", "$API_KEY")
                    val response = inputStream.bufferedReader().use { it.readText() }
                    Log.i(TAG, "$response")
                    withContext(Dispatchers.Main) {
                        val parsedResponse = Gson().fromJson(response, StatusResponse::class.java)
                        onResult(parsedResponse)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    fun getQuestions(context: Context,
        gameMode: GameModeName,
        numberOfRounds: Int,
        onResult: (Response?) -> Unit
    ) {
        val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val savedLanguageCode =
            sharedPreferences.getString("language_code", "en") ?: "en"
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(QUESTIONS_URL)
            val request = Request(mode = gameMode.name.lowercase(), questions = numberOfRounds, lang = savedLanguageCode)

            try {
                val requestBody = parser.toJson(request)
                Log.i(TAG, "Request Body: $requestBody")

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("x-api-key", "$API_KEY")
                    outputStream.use { os ->
                        os.write(requestBody.toByteArray())
                        os.flush()
                    }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val jsonResponse = inputStream.bufferedReader().use { it.readText() }
                        val response = parser.fromJson(jsonResponse, Response::class.java)
                        withContext(Dispatchers.Main) {
                            onResult(response)
                        }
                    } else {
                        Log.e(TAG, "HTTP error: $responseCode")
                        withContext(Dispatchers.Main) {
                            onResult(null)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in getQuestions: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    fun postScore(
        mode: GameModeName,
        username: String,
        score: Double,
        onResult: (ScoreResponse?) -> Unit,

    ) {

        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(SCORE_URL)
            val request = ScoreRequest(mode = mode.name.lowercase(), username = username, score = score)

            try {
                val requestBody = parser.toJson(request)
                Log.i(TAG, "Request Body: $requestBody")

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("x-api-key", "$API_KEY")
                    outputStream.use { os ->
                        os.write(requestBody.toByteArray())
                        os.flush()
                    }

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val jsonResponse = inputStream.bufferedReader().use { it.readText() }
                        val response = parser.fromJson(jsonResponse, ScoreResponse::class.java)
                        withContext(Dispatchers.Main) {
                            onResult(response)
                        }
                    } else {
                        Log.e(TAG, "HTTP error: $responseCode")
                        withContext(Dispatchers.Main) {
                            onResult(null)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in postScore: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    fun getScoresRhythm(onResult: (List<ScoreTop10Response>?) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(TOP10_RHYTHM)
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("x-api-key", "$API_KEY")
                    setRequestProperty("Content-Type", "application/json")

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val jsonResponse = inputStream.bufferedReader().use { it.readText() }
                        println(jsonResponse)

                        val response: List<ScoreTop10Response> = parser.fromJson(
                            jsonResponse,
                            object : TypeToken<List<ScoreTop10Response>>() {}.type
                        )

                        withContext(Dispatchers.Main) {
                            onResult(response)
                        }

                    } else {
                        Log.e(TAG, "HTTP error: $responseCode")
                        withContext(Dispatchers.Main) {
                            onResult(null)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in getScoresRhythm: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    fun getScoresInstrumental(onResult: (List<ScoreTop10Response>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(TOP10_INSTRUMENTAL)
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("x-api-key", "$API_KEY")
                    setRequestProperty("Content-Type", "application/json")

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val jsonResponse = inputStream.bufferedReader().use { it.readText() }
                        println(jsonResponse)

                        val response: List<ScoreTop10Response> = parser.fromJson(
                            jsonResponse,
                            object : TypeToken<List<ScoreTop10Response>>() {}.type
                        )

                        withContext(Dispatchers.Main) {
                            onResult(response)
                        }

                    } else {
                        Log.e(TAG, "HTTP error: $responseCode")
                        withContext(Dispatchers.Main) {
                            onResult(null)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in getScoresInstrumental: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    fun getScoresPitch(onResult: (List<ScoreTop10Response>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(TOP10_PITCH)
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("x-api-key", "$API_KEY")
                    setRequestProperty("Content-Type", "application/json")

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val jsonResponse = inputStream.bufferedReader().use { it.readText() }
                        println(jsonResponse)

                        val response: List<ScoreTop10Response> = parser.fromJson(
                            jsonResponse,
                            object : TypeToken<List<ScoreTop10Response>>() {}.type
                        )

                        withContext(Dispatchers.Main) {
                            onResult(response)
                        }

                    } else {
                        Log.e(TAG, "HTTP error: $responseCode")
                        withContext(Dispatchers.Main) {
                            onResult(null)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in getScoresInstrumental: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    companion object URLs {
        val STATUS_URL = "https://springboot-kotlin-app-84877666332.europe-west1.run.app/api/status"
        val QUESTIONS_URL =
            "https://springboot-kotlin-app-84877666332.europe-west1.run.app/api/audio/questions"
        val SCORE_URL = "https://springboot-kotlin-app-84877666332.europe-west1.run.app/api/scores/add"
        val TOP10_RHYTHM = "https://springboot-kotlin-app-84877666332.europe-west1.run.app/api/scores/top10/rhythm"
        val TOP10_PITCH = "https://springboot-kotlin-app-84877666332.europe-west1.run.app/api/scores/top10/pitch"
        val TOP10_INSTRUMENTAL = "https://springboot-kotlin-app-84877666332.europe-west1.run.app/api/scores/top10/instrumental"
    }
 }
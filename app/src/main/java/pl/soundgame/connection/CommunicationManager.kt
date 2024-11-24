package pl.soundgame.connection

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.soundgame.connection.serializedclasses.Question
import pl.soundgame.connection.serializedclasses.Request
import pl.soundgame.connection.serializedclasses.Response
import pl.soundgame.connection.serializedclasses.StatusResponse
import pl.soundgame.modes.GameModeName
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CommunicationManager {
    private var parser = Gson()
    private val TAG = "CommunicationManager"
    fun parseExemplary(){
        val jsonString = """
        {
            "status": "ok",
            "questions": [
                {
                    "question": "What is 2+2?",
                    "correctAnswer": 1,
                    "ans1": "4",
                    "...": "",
                    "ans4": "5",
                    "url": "http://example.com/answer1"
                },
                {
                    "question": "What is 3+3?",
                    "correctAnswer": 3,
                    "ans1": "5",
                    "...": "",
                    "ans4": "6",
                    "url": "http://example.com/answer2"
                }
            ]
        }
        """
        val response = parser.fromJson(jsonString, Response::class.java)
        Log.i(TAG,response.status)
        response.questions?.forEach { question ->
            Log.i(TAG,question.question)
            Log.i(TAG,"${question.correctAnswer}")
        }
    }

    fun getServerStatus(onResult: (StatusResponse?) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val urlToStatus = URL(STATUS_URL)
            try{
                val response = urlToStatus.readText()
                Log.i(TAG, "$response")
                withContext(Dispatchers.Main) {
                    val response = Gson().fromJson(response, StatusResponse::class.java)
                    onResult(response)
                 }
            }catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(null) // Return null in case of an error
                }
            }
        }
    }

    fun getQuestions(
        gameMode: GameModeName,
        numberOfRounds: Int,
        onResult: (Response?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(QUESTIONS_URL)
            val request = Request(mode = gameMode.name.lowercase(), questions = numberOfRounds)

            try {
                val requestBody = Gson().toJson(request)
                Log.i(TAG, "Request Body: $requestBody")

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    outputStream.use { os ->
                        os.write(requestBody.toByteArray())
                        os.flush()
                    }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val jsonResponse = inputStream.bufferedReader().use { it.readText() }
                        Log.i(TAG, "Response: $jsonResponse")


                        val response = Gson().fromJson(jsonResponse, Response::class.java)

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

    companion object URLs {
        val STATUS_URL = "https://springboot-kotlin-app-84877666332.europe-west1.run.app/api/status"
        val QUESTIONS_URL =
            "https://springboot-kotlin-app-84877666332.europe-west1.run.app/api/audio/questions"
    }
 }
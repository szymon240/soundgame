package pl.soundgame.connection

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.soundgame.connection.serializedclasses.Response
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

    fun getServerStatus(onResult: (String?) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val urlToStatus = URL("https://filesamples.com/samples/code/json/sample1.json")
            try{
                val response = urlToStatus.readText()
                Log.i(TAG, "$response")
                withContext(Dispatchers.Main) {
                    onResult(response)
                }
            }catch (e: Exception) {
                // Handle exceptions (e.g., network error)
                withContext(Dispatchers.Main) {
                    onResult(null) // Return null in case of an error
                }
            }
        }
    }
}
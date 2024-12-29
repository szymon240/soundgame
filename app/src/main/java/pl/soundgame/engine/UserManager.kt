package pl.soundgame.engine

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UserManager private constructor(private val context: Context) {

    private val fileName = "user_data.json"
    private var userData: MutableMap<String, Any> = mutableMapOf(
        "nickname" to "",
        "achievements" to mutableListOf<String>(),
        "gameStats" to mutableMapOf<String, Any>(
            "gamesPlayed" to 0,
            "totalScore" to 0.0,
            "highScores" to mutableMapOf(
                "rhythm" to 0.0,
                "instrumental" to 0.0
            )
        )
    )

    // Singleton instance
    companion object {
        @Volatile
        private var INSTANCE: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return INSTANCE ?: synchronized(this) {
                val instance = UserManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    init {
        loadUserData()
    }

    // Load user data from file
    private fun loadUserData() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                val jsonString = file.readText()
                val jsonObject = JSONObject(jsonString)
                userData = jsonObject.toMap().toMutableMap()
                // Ensure structure consistency for gameStats
                val gameStats = userData["gameStats"] as? MutableMap<String, Any>
                gameStats?.apply {
                    this["totalScore"] = (this["totalScore"] as? Number)?.toDouble() ?: 0.0
                    val highScores = this["highScores"] as? MutableMap<String, Any>
                    highScores?.forEach { (key, value) ->
                        highScores[key] = (value as? Number)?.toDouble() ?: 0.0
                    }
                }
                println("User data loaded successfully.")
            } catch (e: Exception) {
                println("Error loading user data: ${e.message}")
            }
        }
    }


    // Save user data to file
    private fun saveUserData() {
        try {
            if (true) {
                val file = File(context.filesDir, fileName)
                val jsonObject = JSONObject(userData as Map<*, *>)
                FileOutputStream(file).use { output ->
                    output.write(jsonObject.toString().toByteArray())
                    output.flush()
                }
                println("User data saved successfully.")
            } else {
                println("User data validation failed.")
            }
        } catch (e: IOException) {
            println("Error saving user data: ${e.message}")
        }
    }

    // Function to log raw contents of the user data file
    fun logRawFileContents() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                val jsonString = file.readText()
                println("Raw User Data from File: \n$jsonString")
            } catch (e: IOException) {
                println("Error reading user data from file: ${e.message}")
            }
        } else {
            println("User data file does not exist.")
        }
    }


    // Validate user data structure before saving
    private fun validateUserData(): Boolean {
        val isValid = (userData["nickname"] is String
                && userData["achievements"] is List<*>
                && userData["gameStats"] is Map<*, *>
                && (userData["gameStats"] as? Map<String, Any>)?.let {
            val gameStats = it
            gameStats["gamesPlayed"] is Int
                    && gameStats["totalScore"] is Double
                    && (gameStats["highScores"] as? Map<String, Any>)?.let { scores ->
                scores["rhythm"] is Double && scores["instrumental"] is Double
            } == true
        } == true)

        // Log user data structure for debugging
        Log.i("UserManager", "User data validation: $isValid")
        Log.i("UserManager", "User data: $userData")

        return isValid
    }


    // Add or update nickname
    fun setNickname(newNickname: String) {
        if (userData["nickname"] != newNickname) {
            userData["nickname"] = newNickname
            saveUserData()
        }
    }

    // Get nickname
    fun getNickname(): String {
        return userData["nickname"] as? String ?: ""
    }

    // Achievement handling
    fun addAchievement(achievement: String) {
        val achievements = userData["achievements"] as? MutableList<String> ?: mutableListOf()
        if (!achievements.contains(achievement)) {
            achievements.add(achievement)
            userData["achievements"] = achievements
            saveUserData()
        }
    }

    fun getAchievements(): List<String> {
        return userData["achievements"] as? List<String> ?: emptyList()
    }

    fun clearAchievements() {
        userData["achievements"] = mutableListOf<String>()
        saveUserData()
    }

    // Game stats handling
    fun updateHighScore(mode: String, score: Double) {
        val gameStats = userData["gameStats"] as? MutableMap<String, Any> ?: mutableMapOf()
        val highScores = gameStats["highScores"] as? MutableMap<String, Double> ?: mutableMapOf()
        val currentHighScore = highScores[mode] ?: 0.0
        if (score > currentHighScore) {
            highScores[mode] = score
            gameStats["highScores"] = highScores
            userData["gameStats"] = gameStats
            saveUserData()
        }
    }

    fun getHighScore(mode: String): Double {
        val gameStats = userData["gameStats"] as? MutableMap<String, Any> ?: mutableMapOf()
        val highScores = gameStats["highScores"] as? Map<String, Double> ?: emptyMap()
        return highScores[mode] ?: 0.0
    }

    // Increment game stats (gamesPlayed, totalScore)
    fun incrementGameStat(stat: String, increment: Int = 1) {
        val gameStats = userData["gameStats"] as? MutableMap<String, Any> ?: mutableMapOf()
        val currentValue = gameStats[stat] as? Int ?: 0
        gameStats[stat] = currentValue + increment
        userData["gameStats"] = gameStats
        saveUserData()
    }

    fun incrementTotalScore(score: Double) {
        val gameStats = userData["gameStats"] as? MutableMap<String, Any> ?: mutableMapOf()
        val currentTotalScore = gameStats["totalScore"] as? Double ?: 0.0
        gameStats["totalScore"] = currentTotalScore + score
        userData["gameStats"] = gameStats
        saveUserData()
    }

    fun getTotalScore(): Double {
        val gameStats = userData["gameStats"] as? MutableMap<String, Any> ?: mutableMapOf()
        return gameStats["totalScore"] as? Double ?: 0.0
    }

    // Reset game stats
    fun resetGameStats() {
        userData["gameStats"] = mutableMapOf<String, Any>(
            "gamesPlayed" to 0,
            "totalScore" to 0.0,
            "highScores" to mutableMapOf(
                "rhythm" to 0.0,
                "instrumental" to 0.0
            )
        )
        saveUserData()
        println("Game stats have been reset.")
    }

    // Debug method to log current user data
    fun logUserData() {
        println("Nickname: ${getNickname()}")
        println("Achievements: ${getAchievements().joinToString(", ")}")
        println("Games Played: ${getGameStat("gamesPlayed")}")
        println("Total Score: ${getTotalScore()}")
        println("High Scores - Rhythm: ${getHighScore("rhythm")}, Instrumental: ${getHighScore("instrumental")}")
    }

    // Helper method to fetch specific game stat
    fun getGameStat(statName: String): Double {
        val gameStats = userData["gameStats"] as? Map<String, Any> ?: return 0.0
        return (gameStats[statName] as? Number)?.toDouble() ?: 0.0
    }

    // Helper method to convert JSONObject to Map
    private fun JSONObject.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = this[key]
            map[key] = value
        }
        return map
    }
}
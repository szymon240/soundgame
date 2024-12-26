package pl.soundgame.engine

import android.content.Context
import org.json.JSONObject
import java.io.File

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
        }
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


    // Save user data to file
    private fun saveUserData() {
        val file = File(context.filesDir, fileName)
        val jsonObject = JSONObject(userData as Map<*, *>?)
        file.writeText(jsonObject.toString())
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

    // New function to log the content of the file
    fun logUserData() {
        val nickname = getNickname()
        val achievements = getAchievements()
        val gameStats = userData["gameStats"] as? Map<String, Any>

        println("User Data Content:")
        println("Nickname: $nickname")
        println("Achievements: ${if (achievements.isEmpty()) "None" else achievements.joinToString(", ")}")

        println("Game Stats:")
        println("  Games Played: ${gameStats?.get("gamesPlayed") ?: "N/A"}")
        println("  Total Score: ${gameStats?.get("totalScore") ?: "N/A"}")

        val highScores = gameStats?.get("highScores") as? Map<String, Int>
        println("  High Scores:")
        println("    Rhythm: ${highScores?.get("rhythm") ?: "N/A"}")
        println("    Instrumental: ${highScores?.get("instrumental") ?: "N/A"}")
    }

    fun testSaveData() {
        clearAchievements()

        // Log the file contents for verification
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val jsonString = file.readText()
            println("Test file content: $jsonString")
        }
    }
    fun testSaveData2() {
        setNickname("TestUser")
        addAchievement("FirstAchievement")
        addAchievement("SecOndAchievement")

        // Log the file contents for verification
        val file2 = File(context.filesDir, fileName)
        if (file2.exists()) {
            val jsonString = file2.readText()
            println("Test file content: $jsonString")
        }
    }
    fun testSaveData3() {
        setNickname("TesUser")
        addAchievement("FirstAchievement")
        addAchievement("SecondddAchievement")

        // Log the file contents for verification
        val file3 = File(context.filesDir, fileName)
        if (file3.exists()) {
            val jsonString = file3.readText()
            println("Test file content: $jsonString")
        }
    }

    fun getGameStat(stat: String): Int {
        val gameStats = userData["gameStats"] as? Map<String, Any> ?: emptyMap()
        return gameStats[stat] as? Int ?: 0
    }


    // Function to reset game stats
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

    // Convert JSONObject to Map
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
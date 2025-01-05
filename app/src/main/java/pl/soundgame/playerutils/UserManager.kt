package pl.soundgame.playerutils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UserManager private constructor(private val context: Context) {

    private val fileName = "user_data.json"
    private var userData: UserData = UserData()
    private val gson = Gson()

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
                userData = gson.fromJson(jsonString, UserData::class.java)
                Log.i("UserManager", "User data loaded successfully.")
            } catch (e: Exception) {
                Log.e("UserManager", "Error loading user data: ${e.message}")
            }
        }
    }

    // Save user data to file
    private fun saveUserData() {
        try {
            val file = File(context.filesDir, fileName)
            val jsonString = gson.toJson(userData)
            FileOutputStream(file).use { output ->
                output.write(jsonString.toByteArray())
                output.flush()
            }
            Log.i("UserManager", "User data saved successfully.")
        } catch (e: IOException) {
            Log.e("UserManager", "Error saving user data: ${e.message}")
        }
    }

    // Set nickname
    fun setNickname(newNickname: String) {
        if (userData.nickname != newNickname) {
            userData.nickname = newNickname
            saveUserData()
        }
    }

    // Get nickname
    fun getNickname(): String = userData.nickname

    // Add achievement
    fun addAchievement(achievement: String) {
        if (!userData.achievements.contains(achievement)) {
            userData.achievements.add(achievement)
            saveUserData()
        }
    }

    // Get achievements
    fun getAchievements(): List<String> = userData.achievements

    // Clear achievements
    fun clearAchievements() {
        userData.achievements.clear()
        saveUserData()
    }

    // Update high score
    fun updateHighScore(mode: String, score: Double) {
        when (mode) {
            "rhythm" -> if (score > userData.rhythm) userData.rhythm = score
            "instrumental" -> if (score > userData.instrumental) userData.instrumental = score
            "pitch" -> if (score > userData.pitch) userData.pitch = score
        }
        saveUserData()
    }

    // Get high score
    fun getHighScore(mode: String): Double {
        return when (mode) {
            "rhythm" -> userData.rhythm
            "instrumental" -> userData.instrumental
            "pitch" -> userData.pitch
            else -> 0.0
        }
    }

    // Increment games played
    fun incrementGamesPlayed(increment: Int = 1) {
        userData.gamesPlayed += increment
        saveUserData()
    }

    fun getGamesPlayed() : Int = userData.gamesPlayed

    // Increment total score
    fun incrementTotalScore(score: Double) {
        userData.totalScore += score
        saveUserData()
    }

    // Get total score
    fun getTotalScore(): Double = userData.totalScore

    // Reset game stats
    fun resetGameStats() {
        userData.gamesPlayed = 0
        userData.totalScore = 0.0
        userData.rhythm = 0.0
        userData.instrumental = 0.0
        userData.pitch = 0.0
        saveUserData()
        Log.i("UserManager", "Game stats have been reset.")
    }

    // Log current user data
    fun logUserData() {
        Log.i("UserManager", "User Data: ${gson.toJson(userData)}")
    }
}

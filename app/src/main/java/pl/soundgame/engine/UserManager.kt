package pl.soundgame.engine

import android.content.Context
import org.json.JSONObject
import java.io.File

class UserManager private constructor(private val context: Context) {

    private val fileName = "user_data.json"
    private var userData: MutableMap<String, Any> = mutableMapOf(
        "nickname" to "",
        "achievements" to mutableListOf<String>()
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
            // Ensure "achievements" is always a mutable list for modification
            if (userData["achievements"] !is MutableList<*>) {
                userData["achievements"] = mutableListOf<String>()
            }
        }
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

    // Add an achievement
    fun addAchievement(achievement: String) {
        val achievements = userData["achievements"] as? MutableList<String> ?: mutableListOf()
        if (!achievements.contains(achievement)) {
            achievements.add(achievement)
            userData["achievements"] = achievements
            saveUserData()
        }
    }

    // Get achievements
    fun getAchievements(): List<String> {
        return userData["achievements"] as? List<String> ?: emptyList()
    }

    // Function to clear all achievements
    fun clearAchievements() {
        val achievements = mutableListOf<String>()
        userData["achievements"] = achievements
        saveUserData()
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
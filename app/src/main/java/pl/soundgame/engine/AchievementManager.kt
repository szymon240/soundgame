package pl.soundgame.engine

class AchievementManager private constructor(private val userManager: UserManager) {

    private val achievements = listOf(
        Achievement(
            name = "First Steps",
            description = "Complete your first game.",
            requirement = { 2 >= 1 }
        ),
        Achievement(
            name = "High Scorer",
            description = "Score 100 points in a single game.",
            requirement = { 99 >= 100 }
        ),
        Achievement(
            name = "Dedicated Player",
            description = "Play 10 games.",
            requirement = { 10 >= 10 }
        ),
        Achievement(
            name = "Score Chaser",
            description = "Achieve a total score of 300.",
            requirement = { 500 >= 300 }
        )
    )

    // Singleton instance
    companion object {
        @Volatile
        private var INSTANCE: AchievementManager? = null

        fun getInstance(userManager: UserManager): AchievementManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AchievementManager(userManager)
                INSTANCE = instance
                instance
            }
        }
    }

    // Check and unlock all achievements
    fun checkAndUnlockAchievements() {
        val userAchievements = userManager.getAchievements().toMutableSet()

        achievements.forEach { achievement ->
            // Skip already unlocked achievements
            if (!userAchievements.contains(achievement.name) && achievement.requirement()) {
                userManager.addAchievement(achievement.name)
                userAchievements.add(achievement.name)
                // Log or notify about unlocked achievement
                println("Unlocked achievement: ${achievement.name}")
            }
        }
    }

    // Check remaining achievements
    fun checkRemainingAchievements(): List<Achievement> {
        val userAchievements = userManager.getAchievements().toSet()
        val remainingAchievements = achievements.filter { !userAchievements.contains(it.name) }

        // Print remaining achievements
        println("Remaining Achievements:")
        remainingAchievements.forEach { achievement ->
            println("Name: ${achievement.name}, Description: ${achievement.description}")
        }

        return remainingAchievements
    }
}

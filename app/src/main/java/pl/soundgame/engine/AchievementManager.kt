package pl.soundgame.engine

class AchievementManager private constructor(private val userManager: UserManager) {

    private val achievements = listOf(
        Achievement(
            name = "First Steps",
            description = "Complete your first game.",
            requirement = { userManager.getGameStat("gamesPlayed") >= 1 }
        ),
        Achievement(
            name = "High Scorer",
            description = "Score 100 points in a single game.",
            requirement = { userManager.getGameStat("highScores") >= 100 }
        ),
        Achievement(
            name = "Dedicated Player",
            description = "Play 10 games.",
            requirement = { userManager.getGameStat("gamesPlayed") >= 10 }
        ),
        Achievement(
            name = "Score Chaser",
            description = "Achieve a total score of 300.",
            requirement = { userManager.getGameStat("totalScore") >= 300 }
        )
    )

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

    fun checkAndUnlockAchievements() {
        val userAchievements = userManager.getAchievements().toMutableSet()

        achievements.forEach { achievement ->
            if (!userAchievements.contains(achievement.name) && achievement.requirement()) {
                userManager.addAchievement(achievement.name)
                userAchievements.add(achievement.name)
                println("Unlocked achievement: ${achievement.name}")
            }
        }
    }

    fun checkRemainingAchievements(): List<Achievement> {
        val userAchievements = userManager.getAchievements().toSet()
        val remainingAchievements = achievements.filter { !userAchievements.contains(it.name) }

        println("Remaining Achievements:")
        remainingAchievements.forEach { achievement ->
            println("Name: ${achievement.name}, Description: ${achievement.description}")
        }

        return remainingAchievements
    }
}

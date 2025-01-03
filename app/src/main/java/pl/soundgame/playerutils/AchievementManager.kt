package pl.soundgame.playerutils

class AchievementManager private constructor(private val userManager: UserManager) {

    private val achievements = listOf(
        Achievement(
            name = "First Steps",
            description = "Complete your first game.",
            requirement = { userManager.getGamesPlayed() >= 1 },
            textureName = "achievements/first_steps.png"
        ),
        Achievement(
            name = "Dedicated Player",
            description = "Play 10 games.",
            requirement = { userManager.getGamesPlayed() >= 10 },
            textureName = "achievements/dedicated_player.png"
        ),
        Achievement(
            name = "High Scorer - Rhythm",
            description = "Score 100 points in Rhythm Mode.",
            requirement = { userManager.getHighScore("rhythm") >= 100 },
            textureName = "achievements/rhythm_achievement.png"
        ),
        Achievement(
            name = "High Scorer - Instrumental",
            description = "Score 400 points in Instrumental Mode.",
            requirement = { userManager.getHighScore("instrumental") >= 400 },
            textureName = "achievements/instrumental_achievement.png"
        ),
        Achievement(
            name = "High Scorer - Pitch",
            description = "Score 300 points in Pitch Mode.",
            requirement = { userManager.getHighScore("pitch") >= 300 },
            textureName = "achievements/pitch_achievement.png"
        ),
        Achievement(
            name = "Score Chaser",
            description = "Achieve a total score of 300.",
            requirement = { userManager.getTotalScore() >= 300 },
            textureName = "achievements/high_scorer.png"
        )
    )

    private var remainingAchievements = mutableListOf<Achievement>()

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

    /**
     * Checks and unlocks if any achievement should be unlocked after the game - Must be called after finished game
     *
     * @return list of newly unlocked achievements
     */
    fun checkAndUnlockAchievements(): List<Achievement> {
        val userAchievements = userManager.getAchievements().toMutableSet()
        val newAchievements = mutableListOf<Achievement>()
        achievements.forEach { achievement ->
            val isUnlocked = achievement.requirement()
            //println("Checking achievement: ${achievement.name}, Unlocked: $isUnlocked")

            if (!userAchievements.contains(achievement.name) && isUnlocked) {
                userManager.addAchievement(achievement.name)
                userAchievements.add(achievement.name)
                newAchievements.add(achievement)

               // println("Unlocked achievement: ${achievement.name}")
            }
        }
        newAchievements.forEach {achievement ->
            remainingAchievements.add(achievement)
        }
        return newAchievements
    }


    fun checkRemainingAchievements(): List<Achievement> {
        val userAchievements = userManager.getAchievements().toSet()
        val remainingAchievements = achievements.filter { !userAchievements.contains(it.name) }

      //  println("Remaining Achievements:")
        remainingAchievements.forEach { achievement ->
            //println("Name: ${achievement.name}, Description: ${achievement.description}")
        }

        return remainingAchievements
    }

    fun getNotDisplayedAchievements(): List<Achievement>{
        val achievementsToDisplay = remainingAchievements
        remainingAchievements = mutableListOf()
        return achievementsToDisplay
    }

    fun getAllAchievements(): List<Achievement>{
        return this.achievements
    }
}

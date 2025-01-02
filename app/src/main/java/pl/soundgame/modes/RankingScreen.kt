package pl.soundgame.modes

import android.content.Context
import pl.soundgame.R
import pl.soundgame.connection.CommunicationManager
import pl.soundgame.connection.serializedclasses.ScoreTop10Response
import pl.soundgame.engine.Game
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.playerutils.AchievementManager
import pl.soundgame.playerutils.UserManager

class RankingScreen(var context: Context, private val commManager: CommunicationManager, private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    var instrumentalModeScores: List<ScoreTop10Response> = emptyList()
    var rhythmModeScores: List<ScoreTop10Response> = emptyList()
    override fun returnGameModeScene(): Scene {
        commManager.getScoresRhythm { response ->
            response?.let {
                rhythmModeScores = response
                for( row in it){
                    println("${row.playerName} - ${row.score}" )
                }
            }
        }
        commManager.getScoresInstrumental { response ->
            response?.let {
            for( row in it){
                instrumentalModeScores = response
                println("${row.playerName} - ${row.score}" )
            }
        } }

        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }

           // val obj = GameObject(loadTextureBitmap("bledna.png", context) )
            val achievements = displayAchievments()
            for( achievement in achievements) {
                scene.addGameObject(achievement)
            }
            scene.addGameObject( exitButton)
        }

        return scene
    }

    fun displayAchievments(): List<GameObject>{
        val achievements = AchievementManager.getInstance(UserManager.getInstance(context)).getAllAchievements()
        val remaining = AchievementManager.getInstance(UserManager.getInstance(context)).checkRemainingAchievements()
        val gameObjects = mutableListOf<GameObject>()

        val columns = 2
        val columnWidth = 0.8f
        val rowHeight = 0.5f

        for((displayId, achievement) in achievements.withIndex()){
            var text = ""
            val bitmap = if (achievement in remaining){
                text = context.getString(R.string.achievement_locked)
                loadTextureBitmap("achievements/locked_achievement.png", context)
            } else {
                text =  achievement.name
                loadTextureBitmap(achievement.textureName, context)
            }
            val gameObject = GameObject(bitmap)
            val x = (-0.4f +  columnWidth * (displayId%columns))
            val y = (0.5f - rowHeight * (displayId/columns))
            gameObject.setOriginPosition(x = x , y= y)
            gameObject.scale(0.3f)
            gameObjects.add(gameObject)
            val title = TextBox(initialText = text, size = 16f)
            title.setOriginPosition(y = y - rowHeight/2, x= x)
            title.scale(0.5f)
            gameObjects.add(title)
        }

        return gameObjects
    }
}
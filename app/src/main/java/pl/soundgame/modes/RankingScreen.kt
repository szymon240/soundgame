package pl.soundgame.modes

import android.content.Context
import pl.soundgame.connection.CommunicationManager
import pl.soundgame.connection.serializedclasses.ScoreTop10Response
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.loadTextureBitmap

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

            val obj = GameObject(loadTextureBitmap("bledna.png", context) )

            scene.addGameObject(obj, exitButton)
        }

        return scene
    }
}
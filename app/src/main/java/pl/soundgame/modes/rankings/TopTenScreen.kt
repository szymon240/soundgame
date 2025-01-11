package pl.soundgame.modes.rankings

import android.content.Context
import android.util.Log
import pl.soundgame.R
import pl.soundgame.connection.CommunicationManager
import pl.soundgame.connection.serializedclasses.ScoreTop10Response
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.modes.GameMode
import pl.soundgame.modes.GameModeName

class TopTenScreenInstrumental(var context: Context,
                               private val commManager: CommunicationManager,
                               private val changeModeCallback: (GameModeName) -> Unit) : GameMode()  {
    private var instrumentalModeScores: List<ScoreTop10Response> = emptyList()
    override fun returnGameModeScene(): Scene {
        val scene = Scene()

        commManager.getScoresInstrumental { response ->
            response?.let {
                instrumentalModeScores = response

                for( row in it){
                    println("${row.playerName} - ${row.score}" )
                }
                val ranks = displayRanking(response, context)
                for (rank in ranks){
                    scene.addGameObject(rank)
                }
            }
        }

        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.RANKING_SCREEN) }

            scene.addGameObject( exitButton)
        }

        return scene
    }
}

class TopTenScreenPitch(var context: Context,
                               private val commManager: CommunicationManager,
                               private val changeModeCallback: (GameModeName) -> Unit) : GameMode()  {
    private var instrumentalModeScores: List<ScoreTop10Response> = emptyList()
    override fun returnGameModeScene(): Scene {
        val scene = Scene()

        commManager.getScoresPitch { response ->
            response?.let {
                instrumentalModeScores = response

                for( row in it){
                    println("${row.playerName} - ${row.score}" )
                }
                val ranks = displayRanking(response, context)
                for (rank in ranks){
                    scene.addGameObject(rank)
                }
            }
        }

        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.RANKING_SCREEN) }

            scene.addGameObject( exitButton)
        }

        return scene
    }
}

class TopTenScreenRhythm(var context: Context,
                         private val commManager: CommunicationManager,
                         private val changeModeCallback: (GameModeName) -> Unit) : GameMode() {
    private var rhythmModeScores: List<ScoreTop10Response> = emptyList()
    override fun returnGameModeScene(): Scene {
        val scene = Scene()

        commManager.getScoresRhythm { response ->
            response?.let {
                rhythmModeScores = response


                for( row in it){
                    println("${row.playerName} - ${row.score}" )

                }
                val ranks = displayRanking(response, context)
                for (rank in ranks){
                    scene.addGameObject(rank)
                }
            }
        }

        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.RANKING_SCREEN) }
            scene.addGameObject( exitButton)
        }

        return scene
    }
}

private fun displayRanking(scores: List<ScoreTop10Response>, context: Context ): List<GameObject> {
    val gameObjects = mutableListOf<GameObject>()
    val id = "scores"
    val rowHeight = 0.11f
    val idHeader = TextBox(initialText = "",  size = 24f, id = id)
    idHeader.setOriginPosition(x= -0.9f, y = 0.65f   )
    idHeader.scale(0.5f)

    val nameTextHeader = TextBox(initialText = "${context.getString(R.string.achievements_nick)}",  size = 24f, id = id)
    nameTextHeader.setOriginPosition(x= -0.2f, y = 0.65f   )
    nameTextHeader.scale(0.5f)

    val scoreHeader = TextBox(initialText = "${context.getString(R.string.achievements_score)}",  size = 24f, id = id)
    scoreHeader.setOriginPosition(x= 0.6f, y =  0.65f  )
    scoreHeader.scale(0.5f)
    gameObjects.add(idHeader)
    gameObjects.add(scoreHeader)
    gameObjects.add(nameTextHeader)

    for ((displayId, score) in scores.withIndex()) {
        val idText = TextBox(initialText = "${displayId + 1}",  size = 24f, id = id)
        idText.setOriginPosition(x= -0.9f, y = 0.55f -(displayId * rowHeight)  )
        idText.scale(0.5f)

        val nameText = TextBox(initialText = "${score.playerName}",  size = 24f, id = id)
        nameText.setOriginPosition(x= -0.2f, y = 0.55f -(displayId * rowHeight)  )
        nameText.scale(0.5f)

        val score = TextBox(initialText = "${score.score}",  size = 24f, id = id)
        score.setOriginPosition(x= 0.6f, y =  0.55f - (displayId * rowHeight) )
        score.scale(0.5f)

        gameObjects.add(idText)
        gameObjects.add(nameText)
        gameObjects.add(score)
    }

    return gameObjects
}
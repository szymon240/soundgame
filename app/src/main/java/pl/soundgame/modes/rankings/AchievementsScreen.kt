package pl.soundgame.modes.rankings

import android.content.Context
import pl.soundgame.R
import pl.soundgame.connection.CommunicationManager
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.GameObject
import pl.soundgame.engine.gameobjects.PopupAchievement
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.modes.GameMode
import pl.soundgame.modes.GameModeName
import pl.soundgame.playerutils.AchievementManager
import pl.soundgame.playerutils.UserManager

class AchievementsScreen(var context: Context,
                         private val commManager: CommunicationManager,
                         private val changeModeCallback: (GameModeName) -> Unit) : GameMode()  {
    override fun returnGameModeScene(): Scene {
        val scene = Scene()

        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            fun displayAchievements(): List<GameObject>{
                val achievements = AchievementManager.getInstance(UserManager.getInstance(context)).getAllAchievements()
                val remaining = AchievementManager.getInstance(UserManager.getInstance(context)).checkRemainingAchievements()
                val gameObjects = mutableListOf<GameObject>()
                val id = "achievements"
                val columns = 2
                val columnWidth = 0.8f
                val rowHeight = 0.5f
                val popups = mutableListOf<GameObject>()

                for((displayId, achievement) in achievements.withIndex()){
                    val popup = PopupAchievement(background = loadTextureBitmap("popupBackground.png", context),
                        achievement = achievement, context = context, id = "popup $displayId")
                    var text = ""
                    val bitmap = if (achievement in remaining){
                        text = context.getString(R.string.achievement_locked)
                        loadTextureBitmap("achievements/locked_achievement.png", context)
                    } else {
                        popup.setPopupCallback { popup.hidePopup() ; scene.unlockAllButtons() }
                        popups.add(popup)
                        text =  achievement.name
                        loadTextureBitmap(achievement.textureName, context)
                    }
                    val gameObject = GameObject(bitmap,  id = id )
                    val x = (-0.4f +  columnWidth * (displayId%columns))
                    val y = (0.5f - rowHeight * (displayId/columns))
                    gameObject.setOriginPosition(x = x , y= y)
                    gameObject.scale(0.3f)

                    if(achievement !in remaining) {
                        gameObject.onClickAction {
                            popup.showPopup() ; scene.lockAllButtons()
                        }
                    }

                    gameObjects.add(gameObject)
                    val title = TextBox(initialText = text, size = 16f, id = id )
                    title.setOriginPosition(y = y - rowHeight/2, x= x)
                    title.scale(0.5f)
                    gameObjects.add(title)


                }
                gameObjects.addAll(popups)
                return gameObjects
            }

            val exitButton = Button(loadTextureBitmap("back.png", context), id = "playButton")
            exitButton.setOriginPosition(y = 0.8f, x = -0.75f)
            exitButton.scale(0.15f)
            exitButton.onClickAction { changeModeCallback(GameModeName.RANKING_SCREEN) }
            val achievements = displayAchievements()
            for(achievement in achievements)
            {
                scene.addGameObject(achievement)
            }

            scene.addGameObject( exitButton)
        }

        return scene
    }


}
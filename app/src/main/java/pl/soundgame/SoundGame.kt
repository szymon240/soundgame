package pl.soundgame

import android.content.Context
import android.util.Log
import pl.soundgame.connection.CommunicationManager
import pl.soundgame.engine.Game
import pl.soundgame.engine.Scene
import pl.soundgame.modes.GameMode
import pl.soundgame.modes.GameModeName
import pl.soundgame.modes.InstrumentalMode
import pl.soundgame.modes.Menu
import pl.soundgame.modes.RhythmMode


internal class SoundGame(context: Context) : Game() {
    override var mScene: Scene
    private var gameMode: GameMode
    private var context: Context
    private var gameModeName: GameModeName
    private var TAG = "SoundGame Main Object"
    private var changeModeCallback: (GameModeName) -> Unit = { mode -> changeMode(mode)}
    private var rounds = 8
    private val commManager = CommunicationManager()
    init {
        this.context = context
        gameMode = Menu(this.context, changeModeCallback)
        commManager.parseExemplary()
        commManager.getServerStatus { status ->
            if(status != null ) {
                Log.i(TAG, "App: ${status.app}, Database: ${status.database}")
            }
            else{
                Log.i(TAG, "Something's wrong")
            }
        }

        commManager.getQuestions(GameModeName.INSTRUMENTAL, 1) { response ->
            if (response != null) {
                Log.i(TAG, "Question status: ${response.status}")
                Log.i(TAG, "Questions: ${response.questions}")
            } else {
                Log.e(TAG, "Failed to fetch questions")
            }
        }
        gameModeName = GameModeName.MENU
        mScene = gameMode.returnGameModeScene()
        changeMode(GameModeName.MENU)
    }

    fun changeMode(newMode: GameModeName) {
        gameMode = when (newMode) {
            GameModeName.MENU -> Menu(this.context, changeModeCallback)
            GameModeName.RHYTHM -> RhythmMode(rounds, this.context, changeModeCallback)
            GameModeName.INSTRUMENTAL -> InstrumentalMode(this.context, changeModeCallback)
            //GameModeName.ANOTHER_MODE -> AnotherMode(this.context)

        }
        Log.i(TAG, "Swaped mode to: ${newMode.name}")
        gameModeName = newMode
        mScene = gameMode.returnGameModeScene()
        mScene.loadScene()
    }
}


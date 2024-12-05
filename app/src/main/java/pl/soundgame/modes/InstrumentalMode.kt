package pl.soundgame.modes

import android.content.Context
import android.util.Log
import pl.soundgame.R
import pl.soundgame.SoundPlayer
import pl.soundgame.connection.serializedclasses.Question
import pl.soundgame.engine.Scene
import pl.soundgame.engine.background.SampleBackground
import pl.soundgame.engine.gameobjects.Button
import pl.soundgame.engine.gameobjects.TextBox
import pl.soundgame.engine.loadTextureBitmap
import pl.soundgame.engine.shapes.createTextTexture

class InstrumentalMode(
    var context: Context,
    private val changeModeCallback: (GameModeName) -> Unit,
    private var questions: List<Question>, // List of questions passed into the mode
    private val totalRounds: Int
) : GameMode() {

    private val soundPlayer: SoundPlayer = SoundPlayer(context)
    private var currentRound = 0
    private var score = 0

    override fun returnGameModeScene(): Scene {
        val scene = Scene()
        scene.setBackground {
            SampleBackground(context)
        }

        scene.setInitScene {
            setupScene(scene)
        }
        return scene
    }

    val scoreExampleText = context.getString(R.string.score_example_text)
    val roundExampleText = context.getString(R.string.round_example_text)
    val final_score_text = context.getString(R.string.final_score_text)
    val finish_game_text = context.getString(R.string.finish_game_text)

    private fun setupScene(scene: Scene) {
        var scoreText = TextBox(initialText = "$scoreExampleText $score", id = "scoreText")
        var roundText = TextBox(initialText = "$roundExampleText $currentRound", id = "roundText")

        roundText.setOriginPosition(y = 0.8f, x = 0f)
        roundText.scale(0.5f)
        scoreText.setOriginPosition(y = 0.7f, x = 0f)
        scoreText.scale(0.5f)
        scene.addGameObject(scoreText, roundText)

        lateinit var  ans1: Button; lateinit var ans2: Button; lateinit var ans3: Button; lateinit var ans4: Button
        var currentQuestion = questions.getOrNull(currentRound)
        val playMusicButton = Button(loadTextureBitmap("button.png", context), id = "playMusicButton")

        fun refreshQuestion() {
            // Create and load a new scene with the next question
            val q = questions[currentRound]
            ans1.changeBaseBitmap(createTextTexture(text = "${q.ans1}",
                size = 90f,
                background = loadTextureBitmap("button.png", context)))
            ans2.changeBaseBitmap(createTextTexture(text = "${q.ans2}",
                size = 90f,
                background = loadTextureBitmap("button.png", context)))
            ans3.changeBaseBitmap(createTextTexture(text = "${q.ans3}",
                size = 90f,
                background = loadTextureBitmap("button.png", context)))
            ans4.changeBaseBitmap(createTextTexture(text = "${q.ans4}",
                size = 90f,
                background = loadTextureBitmap("button.png", context)))

            playMusicButton.onClickAction { soundPlayer.playFromUrl(q.url) }
            for ((index, question) in questions.withIndex()) {
                Log.i(
                    "Insytr", """
                |Question ${index}:
                |  Question Text: ${question.question}
                |  Correct Answer: ${question.correctAnswer}
                |  Answer 1: ${question.ans1}
                |  Answer 2: ${question.ans2}
                |  Answer 3: ${question.ans3}
                |  Answer 4: ${question.ans4}
                |  URL: ${question.url}
                """.trimMargin()
                )
            }

        }

        fun getCorrectAnswer(question: Question): String? {
            return when (question.correctAnswer) {
                1 -> question.ans1 ?: ""
                2 -> question.ans2 ?: ""
                3 -> question.ans3 ?: ""
                4 -> question.ans4 ?: ""
                else -> ""
            }
        }

        fun checkAnswer(selectedAnswer: Int) {
            val currentQuestion = questions.getOrNull(currentRound)
            if (currentQuestion != null) {
                if (selectedAnswer == currentQuestion.correctAnswer) {
                    println("Correct! The answer was $selectedAnswer.")
                    score++
                } else {
                    println("Wrong! The correct answer was ${getCorrectAnswer(currentQuestion)}.")
                }
                currentRound++
                if (currentRound < totalRounds) {
                    refreshQuestion()
                } else {
                    // End of game, show the final score and transition to the menu
                    println("Game Over! Your final score: $score")
                }
            }
        }

        fun createAnswerButton(scene: Scene, answer: Int, x: Float, y: Float): Button {
            val button = Button(loadTextureBitmap("button.png", context),
                    id = "answerButton-$answer")
            button.setOriginPosition(x = x, y = y)
            button.scale(0.25f)
            button.onClickAction { checkAnswer(answer) }
            return button
        }

        if (currentRound >= totalRounds) {
            println("Game Over! Your score: $score")
            return
        }

        if (currentQuestion == null) {
            println("No more questions available!")
            return
        }

        println("Displaying Question: ${currentQuestion.question}")

        // Create button to play the music/sound
        playMusicButton.setOriginPosition(x = 0.0f, y = 0.4f)
        playMusicButton.scale(0.25f)
        playMusicButton.onClickAction { soundPlayer.playFromUrl(currentQuestion.url) }
        scene.addGameObject(playMusicButton)

        // Create answer buttons for the question
        ans1 = createAnswerButton(scene, 1, x = -0.5f, y = -0.2f)
        ans2 = createAnswerButton(scene, 2, x = 0.5f, y = -0.2f)
        ans3 = createAnswerButton(scene, 3, x = -0.5f, y = -0.6f)
        ans4 = createAnswerButton(scene, 4, x = 0.5f, y = -0.6f)

        // Exit button
        val exitButton = Button(loadTextureBitmap("button.png", context), id = "exitButton")
        exitButton.setOriginPosition(y = 0.85f, x = -0.65f)
        exitButton.scale(0.2f)
        exitButton.onClickAction { changeModeCallback(GameModeName.MENU) }
        scene.addGameObject(exitButton, ans1, ans2, ans3, ans4)
        refreshQuestion()
    }


}

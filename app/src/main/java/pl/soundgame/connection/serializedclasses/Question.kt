package pl.soundgame.connection.serializedclasses

data class Question(
    val question: String,
    val correctAnswer: Int,
    val ans1: String?,
    val ans2: String?,
    val ans3: String?,
    val ans4: String?,
    val url: String
)

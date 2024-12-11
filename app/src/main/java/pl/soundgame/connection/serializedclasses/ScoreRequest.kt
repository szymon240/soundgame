package pl.soundgame.connection.serializedclasses

data class ScoreRequest(
    val mode: String,
    val username: String,
    val score: Double,
)
package pl.soundgame.connection.serializedclasses

data class ScoreTop10Response(
    val id: Int,
    val playerName: String,
    val score: Double,
    val mode: ModeResponse
)

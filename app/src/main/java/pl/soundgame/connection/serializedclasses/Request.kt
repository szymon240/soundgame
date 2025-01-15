package pl.soundgame.connection.serializedclasses

data class Request(
    val mode: String,
    val questions: Int,
    val lang: String? = null
)
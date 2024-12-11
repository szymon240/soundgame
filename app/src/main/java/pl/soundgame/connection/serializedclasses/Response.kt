package pl.soundgame.connection.serializedclasses

data class Response(
    val status: String,
    val questions: List<Question>?
)

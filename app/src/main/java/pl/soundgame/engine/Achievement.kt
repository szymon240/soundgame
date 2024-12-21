package pl.soundgame.engine

data class Achievement(
    val name: String,
    val description: String,
    val requirement: () -> Boolean
)

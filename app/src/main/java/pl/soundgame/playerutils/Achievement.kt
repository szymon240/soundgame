package pl.soundgame.playerutils

data class Achievement(
    val name: String,
    val description: String,
    val requirement: () -> Boolean
)

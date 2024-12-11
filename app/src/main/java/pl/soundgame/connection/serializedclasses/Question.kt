package pl.soundgame.connection.serializedclasses

/**
 * Represents a sound resource in the application.
 *
 * @property id Unique identifier for the sound.
 * @property name A descriptive name for the sound.
 * @property resId The resource ID of the sound file in the app's resources.
 *
 * @author Adam Czyżak
 */
data class Question(
    val question: String,
    val correctAnswer: Int,
    val ans1: String?,
    val ans2: String?,
    val ans3: String?,
    val ans4: String?,
    val url: String
)

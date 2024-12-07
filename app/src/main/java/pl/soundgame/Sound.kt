package pl.soundgame

/**
 * Represents a sound resource in the application.
 *
 * @property id Unique identifier for the sound.
 * @property name A descriptive name for the sound.
 * @property resId The resource ID of the sound file in the app's resources.
 *
 * @author Szymon Szymankiewicz
 */
data class Sound(
    val id: Int,
    val name: String,
    val resId: Int
)

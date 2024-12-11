package pl.soundgame

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import java.io.File

/**
 * A utility class for managing sound playback in the application.
 * Supports playback from both local resources and remote URLs, as well as pitch adjustments.
 *
 * @param context The context used to access app resources and system services.
 *
 * @author Szymon Szymankiewicz
 */
class SoundPlayer(private val context: Context) {

    internal var mediaPlayer: MediaPlayer? = null
    private var currentSoundResId: Int? = null
    private var pausedPosition: Int = 0

    private val soundList = listOf(
        Sound(id = 1, name = "Test Sound", resId = R.raw.test),
        Sound(id = 2, name = "Sound 2", resId = R.raw.sound2),
        Sound(id = 3, name = "Beat 1", resId = R.raw.beat1),
        Sound(id = 4, name = "Beat 2", resId = R.raw.beat2),
        Sound(id = 5, name = "Beat 3", resId = R.raw.beat3)
    )

    /**
     * Changes the current sound and reinitializes the MediaPlayer.
     * Releases the previous MediaPlayer instance if necessary.
     *
     * @param mediaResId Resource ID of the new sound to play.
     */
    fun setSound(mediaResId: Int) {
        if (mediaResId != currentSoundResId) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, mediaResId)
            currentSoundResId = mediaResId
            pausedPosition = 0
            mediaPlayer?.setOnPreparedListener {
                println("Hot to go!")
            }
        }
    }

    /**
     * Releases the MediaPlayer resources and sets it to null.
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Streams and plays audio from a given URL.
     *
     * @param url The URL of the audio to stream.
     */
    fun playFromUrl(url: String) {
        mediaPlayer?.release()
        mediaPlayer = null

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    start()
                }
                setOnErrorListener { _, what, extra ->
                    println("Error occurred: what=$what, extra=$extra")
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            println("Error initializing MediaPlayer: ${e.message}")
        }
    }

    /**
     * Plays a sound with a specific pitch. Supports local resources and URLs.
     *
     * @param pitch The pitch to apply during playback.
     * @param source The sound source, either a resource ID (Int) or a URL (String).
     */
    fun playSoundWithPitch(pitch: Float, source: Any) {
        if (source is String) {
            playFromUrl(source)
            mediaPlayer?.setOnPreparedListener {
                val playbackParams = PlaybackParams().apply { this.pitch = pitch }
                mediaPlayer?.playbackParams = playbackParams
                mediaPlayer?.start()
            }
        } else if (source is Int) {
            setSound(source)
            mediaPlayer?.let {
                val playbackParams = PlaybackParams().apply { this.pitch = pitch }
                it.playbackParams = playbackParams
                it.start()
            }
        } else {
            println("Invalid source type. Must be String (URL) or Int (Resource ID).")
        }
    }


    /**
     * Checks if the MediaPlayer is currently playing audio.
     *
     * @return True if playing, false otherwise.
     */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    /**
     * Finds a sound in the predefined list by its ID.
     *
     * @param id The ID of the sound to retrieve.
     * @return The matching Sound object, or null if not found.
     */
    fun getSoundById(id: Int): Sound? {
        return soundList.find { it.id == id }
    }

    /**
     * Stops any currently playing sound and resets the MediaPlayer.
     */
    fun stopAllSounds() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
            } catch (e: IllegalStateException) {
                println("MediaPlayer is not in a valid state to stop: ${e.message}")
            } finally {
                release()
            }
        }
    }

    /**
     * UNUSED FUNCTIONS (FOR FUTURE)
     */

    /**
     * Plays a sound from a local file.
     *
     * @param soundFile The local file containing the audio to play.
     */
    fun playFromFile(soundFile: File) {
        mediaPlayer?.release()
        mediaPlayer = null

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(soundFile.absolutePath)
                setOnPreparedListener {
                    start()
                }
                setOnErrorListener { _, what, extra ->
                    println("Error occurred: what=$what, extra=$extra")
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            println("Error initializing MediaPlayer: ${e.message}")
        }
    }

    /**
     * Initializes the MediaPlayer with a specific resource ID.
     *
     * @param mediaResId Resource ID of the sound to initialize.
     */
    fun initialize(mediaResId: Int) {
        mediaPlayer = MediaPlayer.create(context, mediaResId)
        currentSoundResId = mediaResId
        mediaPlayer?.setOnPreparedListener {
            println("Hot to go!")
        }
    }

    /**
     * Starts sound from the current position.
     */
    fun play() {
        if (!isPlaying()) {
            mediaPlayer?.seekTo(pausedPosition)
            mediaPlayer?.start()
        }
    }

    /**
     * Pauses playback and stores the current position.
     */
    fun pause() {
        if (isPlaying()) {
            pausedPosition = mediaPlayer?.currentPosition ?: 0
            mediaPlayer?.pause()
        }
    }

    /**
     * Resumes playback from the paused position.
     */
    fun resume() {
        mediaPlayer?.let {
            if (!isPlaying()) {
                it.seekTo(pausedPosition)
                it.start()
            }
        }
    }
}

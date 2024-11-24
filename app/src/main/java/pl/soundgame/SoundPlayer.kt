package pl.soundgame

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams

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

    fun initialize(mediaResId: Int) {
        mediaPlayer = MediaPlayer.create(context, mediaResId)
        currentSoundResId = mediaResId
        mediaPlayer?.setOnPreparedListener {
            println("Hot to go!")
        }
    }

    // Creation of media player and setting a new sound
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

    fun play() {
        if (!isPlaying()) {
            mediaPlayer?.seekTo(pausedPosition)
            mediaPlayer?.start()
        }
    }

    fun pause() {
        if (isPlaying()) {
            pausedPosition = mediaPlayer?.currentPosition ?: 0
            mediaPlayer?.pause()
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!isPlaying()) {
                it.seekTo(pausedPosition)
                it.start()
            }
        }
    }

    //Deletion of current sound from mediaPlayer
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun playSoundWithPitch(pitch: Float) {
        val mediaPlayer = this.mediaPlayer
        mediaPlayer?.let {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
            }
            val playbackParams = PlaybackParams()
            playbackParams.pitch = pitch
            mediaPlayer.playbackParams = playbackParams
            mediaPlayer.start()
        }
    }

    //Check if the media player is currently playing
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun getSoundById(id: Int): Sound? {
        return soundList.find { it.id == id }
    }

    fun playFromUrl(url: String) {
        mediaPlayer?.release()
        mediaPlayer = null

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url) // Ustawienie źródła strumienia
                setOnPreparedListener {
                    start() // Rozpocznij odtwarzanie po przygotowaniu
                }
                setOnErrorListener { _, what, extra ->
                    println("Error occurred: what=$what, extra=$extra")
                    false // Zwrot false oznacza, że MediaPlayer nie obsłuży błędu samodzielnie
                }
                prepareAsync() // Przygotowanie odtwarzania w tle
            }
        } catch (e: Exception) {
            println("Error initializing MediaPlayer: ${e.message}")
        }
    }

}

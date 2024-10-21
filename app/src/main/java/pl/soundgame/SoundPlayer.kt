package pl.soundgame

import android.content.Context
import android.media.MediaPlayer

class SoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentSoundResId: Int? = null
    private var pausedPosition: Int = 0

    private val soundList = listOf(
        Sound(id = 1, name = "Test Sound", resId = R.raw.test),
        Sound(id = 2, name = "Sound 2", resId = R.raw.sound2)
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

    //Check if the media player is currently playing
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun getSoundById(id: Int): Sound? {
        return soundList.find { it.id == id }
    }
}

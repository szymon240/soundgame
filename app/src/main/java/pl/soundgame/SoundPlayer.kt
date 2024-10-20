package pl.soundgame

import android.content.Context
import android.media.MediaPlayer

class SoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null


    fun initialize(mediaResId: Int) {
        mediaPlayer = MediaPlayer.create(context, mediaResId)
        mediaPlayer?.setOnPreparedListener {
            println("MediaPlayer is H O T T O G O!")
        }
    }

    // Creation of media player and setting a new sound
    fun setSound(mediaResId: Int) {
        mediaPlayer?.release() // Release any existing sound
        mediaPlayer = MediaPlayer.create(context, mediaResId)
        mediaPlayer?.setOnPreparedListener {
            println("Hot to go!")
        }
    }

    fun play() {
        if (!isPlaying()) {
            mediaPlayer?.start()
        }
    }

    fun pause()  {
        mediaPlayer?.pause()
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
}

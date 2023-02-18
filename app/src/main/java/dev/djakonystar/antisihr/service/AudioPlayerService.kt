package dev.djakonystar.antisihr.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.content.ContextCompat
import dev.djakonystar.antisihr.app.App
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.data.models.PlayerServiceListener
import dev.djakonystar.antisihr.service.notification.MusicService
import java.io.IOException

class AudioPlayerService : Service(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnErrorListener {

    private val audioStatus = AudioStatus()

    private val binder = PlayerServiceBinder()

    private var mediaPlayer: MediaPlayer? = null

    var isPlaying: Boolean = false
        private set

    var isPaused: Boolean = true
        private set

    var currentAudio: AudioResultData? = null
        private set

    var serviceListener: PlayerServiceListener? = null

    inner class PlayerServiceBinder : Binder() {
        val service: AudioPlayerService
            get() = this@AudioPlayerService
    }


    override fun onBind(p0: Intent?): IBinder = binder

    override fun onPrepared(player: MediaPlayer?) {
        val status = updateStatus(currentAudio, AudioStatus.PlayState.PLAY, player?.duration)
        serviceListener?.onPreparedListener(status)
        this.mediaPlayer = player
        this.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        updateTime()
    }

    fun play(audio: AudioResultData): AudioStatus {
        val tempJcAudio = currentAudio
        currentAudio = audio
        var status = AudioStatus()

        val intent  = Intent(this,MusicService::class.java)
        if (audio.url != "") {
            try {
                mediaPlayer?.let {
                    if (isPlaying) {
                        stop()
                        play(audio)
                    } else {
                        if (tempJcAudio !== audio) {
                            stop()
                            play(audio)
                        } else {
                            status = updateStatus(
                                audio, AudioStatus.PlayState.CONTINUE, mediaPlayer!!.duration
                            )
                            updateTime()
                            serviceListener?.onContinueListener(status)
                        }
                    }
                } ?: let {
                    mediaPlayer = MediaPlayer().also {
                        it.setDataSource(audio.url)
                        it.prepareAsync()
                        it.setOnPreparedListener(this)
                        it.setOnBufferingUpdateListener(this)
                        it.setOnCompletionListener(this)
                        it.setOnErrorListener(this)
                        status = updateStatus(audio, AudioStatus.PlayState.PREPARING)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            throw Exception("MUSIC URL MUST NOT BE EMPTY")
        }
        startService(intent)
        return status
    }

    fun pause(auduo: AudioResultData): AudioStatus {
        val status = updateStatus(auduo, AudioStatus.PlayState.PAUSE)
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        serviceListener?.onPausedListener(status)
        return status
    }

    fun stop(): AudioStatus {
        val status = updateStatus(status = AudioStatus.PlayState.STOP)
        serviceListener?.onStoppedListener(status)
        val intent = Intent(this, MusicService::class.java)
        stopService(intent)
        return status
    }

    fun seekTo(time: Int) {
        mediaPlayer?.seekTo(time)
    }

    private fun updateStatus(
        jcAudio: AudioResultData? = null, status: AudioStatus.PlayState, duration: Int? = null
    ): AudioStatus {

        currentAudio = jcAudio
        audioStatus.audio = jcAudio
        audioStatus.playState = status

        if (duration != null) {
            audioStatus.duration = duration
        }

        mediaPlayer?.let {
            audioStatus.currentPosition = it.currentPosition.toLong()
        }


        when (status) {
            AudioStatus.PlayState.PLAY -> {
                try {
                    mediaPlayer?.start()
                    isPlaying = true
                    isPaused = false
                    val intent = Intent(this, MusicService::class.java)
                    startService(intent)
                } catch (exception: Exception) {
                    serviceListener?.onError(exception)
                    exception.printStackTrace()
                }
            }

            AudioStatus.PlayState.STOP -> {
                mediaPlayer?.let {
                    it.stop()
                    it.reset()
                    it.release()
                    mediaPlayer = null
                }

                isPlaying = false
                isPaused = true
            }

            AudioStatus.PlayState.PAUSE -> {
                mediaPlayer?.pause()
                isPlaying = false
                isPaused = true
            }

            AudioStatus.PlayState.PREPARING -> {
                isPlaying = false
                isPaused = true
            }

            AudioStatus.PlayState.PLAYING -> {
                isPlaying = true
                isPaused = false
            }

            else -> { // CONTINUE case
                mediaPlayer?.start()
                isPlaying = true
                isPaused = false
            }
        }
        return audioStatus
    }

    private fun updateTime() {

        object : Thread() {
            override fun run() {
                while (isPlaying) {
                    try {
                        val status = updateStatus(currentAudio, AudioStatus.PlayState.PLAYING)
                        serviceListener?.onTimeChangedListener(status)
                        sleep(1000)
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    fun finalize() {
        onDestroy()
        stopSelf()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        serviceListener?.onCompletedListener()
    }

    override fun onBufferingUpdate(p0: MediaPlayer?, p1: Int) {
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        return false
    }

}
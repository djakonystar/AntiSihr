package dev.djakonystar.antisihr.service.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.session.MediaSession
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.service.notification.MusicBroadcast
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.MusicState
import dev.djakonystar.antisihr.service.AudioPlayerService
import dev.djakonystar.antisihr.service.PlayerServiceConnection
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.data.models.PlayerManagerListener
import dev.djakonystar.antisihr.data.models.PlayerServiceListener
import dev.djakonystar.antisihr.service.notification.MusicService
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


class PlayerManager
private constructor(private val serviceConnection: PlayerServiceConnection) :
    PlayerServiceListener {

    lateinit var context: Context
    private var jcPlayerService: AudioPlayerService? = null
    private var serviceBound = false
    var playlist: ArrayList<AudioResultData> = ArrayList()
    var shuffleModeList: ArrayList<AudioResultData> = ArrayList()
    var currentPositionList: Int = 0
    private val managerListeners: CopyOnWriteArrayList<PlayerManagerListener> =
        CopyOnWriteArrayList()

    var jcPlayerManagerListener: PlayerManagerListener? = null
        set(value) {
            value?.let {
                if (managerListeners.contains(it).not()) {
                    managerListeners.add(it)
                }
            }
            field = value
        }

    val currentAudio: AudioResultData?
        get() = jcPlayerService?.currentAudio

    var currentStatus: AudioStatus? = null
        private set

    var onShuffleMode: Boolean = false

    var repeatCurrAudio: Boolean = false

    companion object {

        @Volatile
        private var INSTANCE: WeakReference<PlayerManager>? = null

        @JvmStatic
        fun getInstance(
            context: Context,
            playlist: ArrayList<AudioResultData>? = null,
            listener: PlayerManagerListener? = null
        ): WeakReference<PlayerManager> = INSTANCE ?: let {
            INSTANCE = WeakReference(PlayerManager(PlayerServiceConnection(context)).also {
                it.context = context
                it.playlist = playlist ?: ArrayList()
                it.jcPlayerManagerListener = listener
            })
            INSTANCE!!
        }
    }

    init {
        initService()
    }

    /**
     * Connects with audio service.
     */
    private fun initService(connectionListener: ((service: AudioPlayerService?) -> Unit)? = null) =
        serviceConnection.connect(playlist = playlist, onConnected = { binder ->
            jcPlayerService = binder?.service.also { service ->
                serviceBound = true
                connectionListener?.invoke(service)
            } ?: throw Exception()
        }, onDisconnected = {
            serviceBound = false
            throw Exception("Disconnection error")
        })

    /**
     * Plays the given [JcAudio].
     * @param jcAudio The audio to be played.
     */
    fun playAudio(jcAudio: AudioResultData, isContinue: Boolean = true) {
        if (playlist.isEmpty()) {
            throw Exception("EMPTY LIST")
        } else {
            jcPlayerService?.let { service ->
                service.serviceListener = this
                service.play(jcAudio, isContinue)
                shuffleModeList.add(jcAudio)
            } ?: initService { service ->
                jcPlayerService = service
                playAudio(jcAudio, isContinue)
            }
        }
    }


    /**
     * Goes to next audio.
     */
    fun nextAudio(isProgrammatically: Boolean) {
        if (playlist.isEmpty()) {
            throw Exception("EMPTY LIST")
        } else {
            jcPlayerService?.let { service ->
                if (repeatCurrAudio && isProgrammatically) {
                    currentAudio?.let {
                        service.seekTo(0)
                        service.onPrepared(service.getMediaPlayer()!!)
                    }
                } else {
                    service.stop()
                    getNextAudio()?.let { service.play(it) } ?: service.finalize()
                }
            }
        }
    }


    /**
     * Goes to previous audio.
     */
    fun previousAudio() {
        if (playlist.isEmpty()) {
            throw Exception("EMPTY LIST")
        } else {
            jcPlayerService?.let { service ->
                service.stop()
                getPreviousAudio().let { service.play(it) }
            }
        }
    }

    /**
     * Pauses the current audio.
     */
    fun pauseAudio() {
        jcPlayerService?.let { service -> currentAudio?.let { service.pause(it) } }
    }

    /**
     * Continues the stopped audio.
     */
    fun continueAudio() {
        if (playlist.isEmpty()) {
            throw Exception("EMPTY LIST")
        } else {
            val audio = jcPlayerService?.currentAudio ?: let { playlist.first() }
            playAudio(audio)
        }
    }


    fun seekTo(time: Int) {
        jcPlayerService?.seekTo(time)
    }


    private fun getNextAudio(): AudioResultData? {
        return if (onShuffleMode) {
            val randomNumber = Random().nextInt(playlist.size)
            playlist[randomNumber]
        } else {
            try {
                shuffleModeList.add(playlist[currentPositionList.inc()])
                playlist[currentPositionList.inc()]
            } catch (e: IndexOutOfBoundsException) {
                currentPositionList = 0
                playlist[currentPositionList]
            }
        }
    }

    private fun getPreviousAudio(): AudioResultData {
        return if (onShuffleMode) {
            try {
                shuffleModeList[shuffleModeList.lastIndex - 1]
            } catch (e: Exception) {
                shuffleModeList.first()
            }
        } else {
            try {
                playlist[currentPositionList.dec()]
            } catch (e: IndexOutOfBoundsException) {
                playlist.first()
            }
        }
    }


    override fun onPreparedListener(status: AudioStatus) {
        currentStatus = status
        updatePositionAudioList()

        for (listener in managerListeners) {
            listener.onPreparedAudio(status)
        }
    }


    override fun onTimeChangedListener(status: AudioStatus) {
        currentStatus = status

        object : Thread() {
            override fun run() {
                for (listener in managerListeners) {
                    listener.onTimeChanged(status)

                    if (status.currentPosition in 1..2 /* Not to call this every second */) {
                        listener.onPlaying(status)
                    }
                }
                sleep(1000)
            }
        }.start()
    }

    override fun onContinueListener(status: AudioStatus) {
        currentStatus = status

        for (listener in managerListeners) {
            listener.onContinueAudio(status)
        }
    }

    override fun onCompletedListener() {
        for (listener in managerListeners) {
            listener.onCompletedAudio()
        }
    }

    override fun onPausedListener(status: AudioStatus) {
        currentStatus = status

        for (listener in managerListeners) {
            listener.onPaused(status)
        }
    }

    override fun onStoppedListener(status: AudioStatus) {
        currentStatus = status

        for (listener in managerListeners) {
            listener.onStopped(status)
        }
    }

    override fun onError(exception: Exception) {
        notifyError(exception)
    }

    /**
     * Notifies errors for the service listeners
     */
    private fun notifyError(throwable: Throwable) {
        for (listener in managerListeners) {
            listener.onJcpError(throwable)
        }
    }

    /**
     * Updates the current position of the audio list.
     */
    private fun updatePositionAudioList() {
        playlist.indices.singleOrNull { playlist[it] == currentAudio }
            ?.let { this.currentPositionList = it } ?: let { this.currentPositionList = 0 }
    }

    fun isPlaying(): Boolean {
        return jcPlayerService?.isPlaying ?: false
    }

    fun isPaused(): Boolean {
        return jcPlayerService?.isPaused ?: true
    }

    fun kill() {
        jcPlayerService?.let {
            it.stop()
            it.stopSelf()
            it.stopForeground(true)
        }

        serviceConnection.disconnect()
        managerListeners.clear()
        INSTANCE = null
    }

    fun removeFromPlayerManagerListener(listener: PlayerManagerListener) {
        managerListeners.remove(listener)
    }
}

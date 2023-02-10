package dev.djakonystar.antisihr.service.manager

import android.content.Context
import android.content.ServiceConnection
import android.os.Handler
import android.os.Looper
import android.util.Log
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.service.AudioPlayerService
import dev.djakonystar.antisihr.service.PlayerServiceConnection
import dev.djakonystar.antisihr.service.models.AudioStatus
import dev.djakonystar.antisihr.service.models.PlayerManagerListener
import dev.djakonystar.antisihr.service.models.PlayerServiceListener
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
    private var currentPositionList: Int = 0
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
        private set

    private var repeatCount = 0

    companion object {

        @Volatile
        private var INSTANCE: WeakReference<PlayerManager>? = null

        @JvmStatic
        fun getInstance(
            context: Context,
            playlist: ArrayList<AudioResultData>? = null,
            listener: PlayerManagerListener? = null
        ): WeakReference<PlayerManager> = INSTANCE ?: let {
            INSTANCE = WeakReference(
                PlayerManager(PlayerServiceConnection(context)).also {
                    it.context = context
                    it.playlist = playlist ?: ArrayList()
                    it.jcPlayerManagerListener = listener
                }
            )
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
        serviceConnection.connect(
            playlist = playlist,
            onConnected = { binder ->
                jcPlayerService = binder?.service.also { service ->
                    serviceBound = true
                    connectionListener?.invoke(service)
                } ?: throw Exception()
            },
            onDisconnected = {
                serviceBound = false
                throw Exception("Disconnection error")
            }
        )

    /**
     * Plays the given [JcAudio].
     * @param jcAudio The audio to be played.
     */
    fun playAudio(jcAudio: AudioResultData) {
        if (playlist.isEmpty()) {
            throw Exception("EMPTY LIST")
        } else {
            jcPlayerService?.let { service ->
                service.serviceListener = this
                service.play(jcAudio)
            } ?: initService { service ->
                jcPlayerService = service
                playAudio(jcAudio)
            }
        }
    }


    /**
     * Goes to next audio.
     */
    fun nextAudio() {
        if (playlist.isEmpty()) {
            throw Exception("EMPTY LIST")
        } else {
            jcPlayerService?.let { service ->
                if (repeatCurrAudio) {
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
                if (repeatCurrAudio) {
                    currentAudio?.let {
                        service.seekTo(0)
                        service.onPrepared(service.getMediaPlayer()!!)
                    }
                } else {
                    service.stop()
                    getPreviousAudio().let { service.play(it) }
                }
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
            playlist[Random().nextInt(playlist.size)]
        } else {
            try {
                playlist[currentPositionList.inc()]
            } catch (e: IndexOutOfBoundsException) {

                null
            }
        }
    }

    private fun getPreviousAudio(): AudioResultData {
        return if (onShuffleMode) {
            playlist[currentPositionList.dec()]
        } else {
            try {
                playlist[currentPositionList.dec()]
            } catch (e: IndexOutOfBoundsException) {
                return playlist.first()
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
     * Handles the repeat mode.
     */
    fun activeRepeat() {
        if (repeatCount == 1) {
            repeatCurrAudio = false
            repeatCount--
            return
        }

        if (repeatCount == 0) {
            repeatCurrAudio = true
            repeatCount = 1
        }
    }

    /**
     * Updates the current position of the audio list.
     */
    private fun updatePositionAudioList() {
        playlist.indices
            .singleOrNull { playlist[it] == currentAudio }
            ?.let { this.currentPositionList = it }
            ?: let { this.currentPositionList = 0 }
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
}

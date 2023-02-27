package dev.djakonystar.antisihr.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked


class PlayerServiceConnection(private val context: Context) : ServiceConnection {

    private var serviceBound = false
    private var onConnected: ((AudioPlayerService.PlayerServiceBinder?) -> Unit)? = null
    private var onDisconnected: ((Unit) -> Unit)? = null

    override fun onServiceDisconnected(name: ComponentName?) {
        serviceBound = false
        onDisconnected?.invoke(Unit)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        serviceBound = true
        onConnected?.invoke(service as AudioPlayerService.PlayerServiceBinder?)
    }

    fun connect(
        playlist: ArrayList<AudioBookmarked>? = null,
        currentAudio: AudioBookmarked? = null,
        onConnected: ((AudioPlayerService.PlayerServiceBinder?) -> Unit)? = null,
        onDisconnected: ((Unit) -> Unit)? = null
    ) {
        this.onConnected = onConnected
        this.onDisconnected = onDisconnected

        if (serviceBound.not()) {
            val intent = Intent(context.applicationContext, AudioPlayerService::class.java)
            context.applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

    fun disconnect() {
        if (serviceBound)
            try {
                context.unbindService(this)
            } catch (e: IllegalArgumentException) {
            }
    }
}

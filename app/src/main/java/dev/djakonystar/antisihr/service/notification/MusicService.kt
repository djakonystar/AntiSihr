package dev.djakonystar.antisihr.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.MusicState
import dev.djakonystar.antisihr.service.manager.PlayerManager

class MusicService : Service() {

    private lateinit var mediaSession: MediaSessionCompat

    private var musicBroadcast: MusicBroadcast? = null


    override fun onBind(intent: Intent?): IBinder {
        return MyBinder()
    }

    override fun onCreate() {
        super.onCreate()
        if (musicBroadcast == null) {
            musicBroadcast = MusicBroadcast()
        }

        mediaSession = MediaSessionCompat(this, "My Music")


        registerReceiver(musicBroadcast, IntentFilter().apply {
            addAction(MusicState.NEXT.name)
            addAction(MusicState.PLAY.name)
            addAction(MusicState.PREVIOUS.name)
        })

        createNotification()
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return START_REDELIVER_INTENT
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, "Now playing song", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationChannel.setSound(null, null)
            notificationChannel.description = "This is a important channel for showing song!!"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun showNotification() {
        val playerManager = PlayerManager.getInstance(this).get()

        val prevIntent =
            Intent(this, musicBroadcast!!.javaClass).setAction(MusicState.PREVIOUS.name)
        val prevPendingIntent = PendingIntent.getBroadcast(
            this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val playIntent = Intent(this, musicBroadcast!!.javaClass).setAction(MusicState.PLAY.name)
        val playPendingIntent = PendingIntent.getBroadcast(
            this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, musicBroadcast!!.javaClass).setAction(MusicState.NEXT.name)
        val nextPendingIntent = PendingIntent.getBroadcast(
            this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder().setState(
                if (playerManager?.isPlaying() == true) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                playerManager?.currentStatus?.currentPosition ?: 0L,
                if (playerManager?.isPlaying() == true) 1.0f else 0f,
                SystemClock.elapsedRealtime()
            ).setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()
        )

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_music).setStyle(
                androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken)
            ).setPriority(Notification.PRIORITY_HIGH).setOnlyAlertOnce(true).setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent).addAction(
                    if (playerManager?.isPlaying()!!) {
                        R.drawable.ic_pause
                    } else {
                        R.drawable.ic_play
                    }, "Play", playPendingIntent
                ).addAction(R.drawable.ic_forward, "Next", nextPendingIntent)

        Glide.with(this).asBitmap().load(playerManager.currentAudio?.image).centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap>?
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        mediaSession.setMetadata(
                            MediaMetadataCompat.Builder().putLong(
                                MediaMetadataCompat.METADATA_KEY_DURATION,
                                playerManager.currentStatus?.duration?.toLong() ?: 0L
                            ).putString(
                                MediaMetadataCompat.METADATA_KEY_TITLE, playerManager.currentAudio?.name
                            ).putString(
                                MediaMetadataCompat.METADATA_KEY_ARTIST,
                                playerManager.currentAudio?.author
                            ).putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, resource).build()
                        )
                    } else {
                        notification.setLargeIcon(resource)
                        notification.setContentTitle(playerManager.currentAudio?.name)
                        notification.setContentText(playerManager.currentAudio?.author)
                    }
                    startForeground(12, notification.build())

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }

    companion object {
        const val CHANNEL_ID = "notify"
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("SSS", "SERVICE DESTROY")
        unregisterReceiver(musicBroadcast)
    }

}
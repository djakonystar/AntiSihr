package dev.djakonystar.antisihr.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.data.models.MusicState
import dev.djakonystar.antisihr.service.manager.PlayerManager

class MusicBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val playerManager = PlayerManager.getInstance(context)

        when (intent.action) {
            MusicState.PLAY.name -> {
                if (playerManager.get()!!.isPlaying()) {
                    Log.d("TTTT","PAUSE AUDIO")
                    playerManager.get()!!.pauseAudio()
                } else {
                    Log.d("TTTT","CONTINUE AUDIO")
                    playerManager.get()!!.continueAudio()
                }
            }
            MusicState.NEXT.name -> try {
                playerManager.get()?.nextAudio()
            } catch (e: Exception) {
                try {
                    playerManager.get()?.continueAudio()
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }

            }
            MusicState.PREVIOUS.name -> try {
                playerManager.get()?.previousAudio()
            } catch (e: Exception) {
                try {
                    playerManager.get()?.continueAudio()
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
        }
    }
}
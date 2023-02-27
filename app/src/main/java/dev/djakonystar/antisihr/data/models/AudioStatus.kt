package dev.djakonystar.antisihr.data.models

import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked

class AudioStatus{
    var audio:AudioBookmarked? = null
    var playState: PlayState = PlayState.PREPARING
    var currentPosition: Long = 0
    var duration: Int = 0




    enum class PlayState {
        PLAY, PAUSE, STOP, CONTINUE, PREPARING, PLAYING
    }
}

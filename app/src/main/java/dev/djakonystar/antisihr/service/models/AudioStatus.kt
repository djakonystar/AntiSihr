package dev.djakonystar.antisihr.service.models

import dev.djakonystar.antisihr.data.models.AudioResultData

class AudioStatus{
    var audio:AudioResultData? = null
    var playState:PlayState = PlayState.PREPARING
    var currentPosition: Long = 0





    enum class PlayState {
        PLAY, PAUSE, STOP, CONTINUE, PREPARING, PLAYING
    }
}

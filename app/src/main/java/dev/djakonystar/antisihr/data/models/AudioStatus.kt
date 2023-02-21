package dev.djakonystar.antisihr.data.models

class AudioStatus{
    var audio:AudioResultData? = null
    var playState: PlayState = PlayState.PREPARING
    var currentPosition: Long = 0
    var duration: Int = 0




    enum class PlayState {
        PLAY, PAUSE, STOP, CONTINUE, PREPARING, PLAYING
    }
}

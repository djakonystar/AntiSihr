package dev.djakonystar.antisihr.data.models


/**
 * This class represents all the [JcPlayerService] callbacks.
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 18/12/17.
 * Jesus loves you.
 */
interface PlayerManagerListener {

    /**
     * Prepares the new audio.
     * @param audioName The audio name.
     * @param duration The audio duration.
     */
    fun onPreparedAudio(status: AudioStatus)

    /**
     * The audio ends.
     */
    fun onCompletedAudio()

    /**
     * The audio is paused.
     */
    fun onPaused(status: AudioStatus)

    /**
     * The audio was paused and user hits play again.
     */
    fun onContinueAudio(status: AudioStatus)

    /**
     *  Called when there is an audio playing.
     */
    fun onPlaying(status: AudioStatus)

    /**
     * Called when the time of the audio changed.
     */
    fun onTimeChanged(status: AudioStatus)


    /**
     * Called when the player stops.
     */
    fun onStopped(status: AudioStatus)

    /**
     * Notifies some error.
     * @param throwable The error.
     */
    fun onJcpError(throwable: Throwable)
}
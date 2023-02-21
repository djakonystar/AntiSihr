package dev.djakonystar.antisihr.data.models


/**
 * @author Jean Carlos (Github: @jeancsanchez)
 * @date 04/08/18.
 * Jesus loves you.
 */
interface PlayerServiceListener {

    /**
     * Notifies on prepared audio for the service listeners
     */
    fun onPreparedListener(status: AudioStatus)

    /**
     * Notifies on time changed for the service listeners
     */
    fun onTimeChangedListener(status: AudioStatus)

    /**
     * Notifies on continue for the service listeners
     */
    fun onContinueListener(status: AudioStatus)

    /**
     * Notifies on completed audio for the service listeners
     */
    fun onCompletedListener()

    /**
     * Notifies on paused for the service listeners
     */
    fun onPausedListener(status: AudioStatus)

    /**
     * Notifies on stopped for the service listeners
     */
    fun onStoppedListener(status: AudioStatus)

    /**
     * Notifies an error for the service listeners
     */
    fun onError(exception: Exception)
}
package dev.djakonystar.antisihr.utils

import android.provider.MediaStore.Audio
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.service.manager.PlayerManager
import kotlinx.coroutines.flow.MutableSharedFlow

val visibilityOfBottomNavigationView = MutableSharedFlow<Boolean>()
val visibilityOfLoadingAnimationView = MutableSharedFlow<Boolean>()
val visibilityAudioPlayer = MutableSharedFlow<Boolean>()

val showBottomNavigationView = MutableSharedFlow<Unit>()


val playAudioFlow = MutableSharedFlow<AudioResultData>()


val closeOfShopBottomSheetFlow = MutableSharedFlow<Unit>()

val preparingAudioFlow = MutableSharedFlow<Pair<PlayerManager, AudioStatus>>()
val pausedAudioFlow = MutableSharedFlow<PlayerManager>()
val playingAudioFlow = MutableSharedFlow<PlayerManager>()
val continueAudioFlow = MutableSharedFlow<PlayerManager>()
val completeAudioFlow = MutableSharedFlow<PlayerManager>()

val audioPlayClickFlow = MutableSharedFlow<PlayerManager>()
val audioNextClickFlow = MutableSharedFlow<PlayerManager>()
val audioContinueClickFlow = MutableSharedFlow<PlayerManager>()
val audioPauseClickFlow = MutableSharedFlow<PlayerManager>()
val audioPreviousClickFlow = MutableSharedFlow<PlayerManager>()

val showBottomPlayerFlow = MutableSharedFlow<Boolean>()

val changeBottomNavItemFlow = MutableSharedFlow<Boolean>()

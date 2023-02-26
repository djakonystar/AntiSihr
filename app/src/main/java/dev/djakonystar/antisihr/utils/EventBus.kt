package dev.djakonystar.antisihr.utils

import android.provider.MediaStore.Audio
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.service.manager.PlayerManager
import kotlinx.coroutines.flow.MutableSharedFlow

val visibilityOfBottomNavigationView = MutableSharedFlow<Boolean>()
val visibilityOfLoadingAnimationView = MutableSharedFlow<Boolean>()

val showBottomNavigationView = MutableSharedFlow<Unit>()


val playAudioFlow = MutableSharedFlow<AudioResultData>()


val closeOfShopBottomSheetFlow = MutableSharedFlow<Unit>()

val showBottomPlayerFlow = MutableSharedFlow<Boolean>()

val changeBottomNavItemFlow = MutableSharedFlow<Boolean>()


val resetBottomPlayerInfoFlow = MutableSharedFlow<Unit>()
val errorBottomPlayerInfoFlow = MutableSharedFlow<Unit>()
package dev.djakonystar.antisihr.utils

import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import kotlinx.coroutines.flow.MutableSharedFlow

val visibilityOfBottomNavigationView = MutableSharedFlow<Boolean>()
val visibilityOfLoadingAnimationView = MutableSharedFlow<Boolean>()

val playAudioFlow = MutableSharedFlow<AudioBookmarked>()


val closeOfShopBottomSheetFlow = MutableSharedFlow<Unit>()

val showBottomPlayerFlow = MutableSharedFlow<Boolean>()

val changeBottomNavItemFlow = MutableSharedFlow<Boolean>()


val resetBottomPlayerInfoFlow = MutableSharedFlow<Unit>()
val errorBottomPlayerInfoFlow = MutableSharedFlow<Unit>()
package dev.djakonystar.antisihr.utils

import dev.djakonystar.antisihr.data.models.AudioResultData
import kotlinx.coroutines.flow.MutableSharedFlow

val visibilityOfBottomNavigationView = MutableSharedFlow<Boolean>()
val visibilityOfLoadingAnimationView = MutableSharedFlow<Boolean>()
val visibilityAudioPlayer = MutableSharedFlow<Boolean>()

val showBottomNavigationView = MutableSharedFlow<Unit>()

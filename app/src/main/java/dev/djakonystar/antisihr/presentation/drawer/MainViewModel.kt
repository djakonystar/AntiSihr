package dev.djakonystar.antisihr.presentation.drawer

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.drawerlayout.AboutAppData
import dev.djakonystar.antisihr.data.models.drawerlayout.AddFeedbackData
import dev.djakonystar.antisihr.data.models.drawerlayout.LanguageData
import kotlinx.coroutines.flow.Flow

interface MainViewModel {
    val sendFeedbackDataSuccesFlow: Flow<GenericResponse<String>>
    val messageFlow:Flow<String>
    val errorFlow: Flow<Throwable>
    suspend fun addFeedback(feedbackData: AddFeedbackData)

    val getLanguagesSuccessFlow:Flow<GenericResponse<List<LanguageData>>>
    suspend fun getLanguages()


    val infoAboutAppFlow:Flow<GenericResponse<List<AboutAppData>>>
    suspend fun getInfoAboutApp()
}
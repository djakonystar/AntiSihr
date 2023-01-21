package dev.djakonystar.antisihr.domain.repository

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.drawerlayout.AboutAppData
import dev.djakonystar.antisihr.data.models.drawerlayout.AddFeedbackData
import dev.djakonystar.antisihr.data.models.drawerlayout.LanguageData
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun addFeedback(feedbackData: AddFeedbackData): Flow<ResultData<GenericResponse<String>>>

    suspend fun getListOfLanguages(): Flow<ResultData<GenericResponse<List<LanguageData>>>>

    suspend fun getInfoAboutApp():Flow<ResultData<GenericResponse<List<AboutAppData>>>>
}
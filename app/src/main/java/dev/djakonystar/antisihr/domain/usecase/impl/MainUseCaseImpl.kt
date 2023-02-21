package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.drawerlayout.AboutAppData
import dev.djakonystar.antisihr.data.models.drawerlayout.AddFeedbackData
import dev.djakonystar.antisihr.data.models.drawerlayout.LanguageData
import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import dev.djakonystar.antisihr.domain.repository.MainRepository
import dev.djakonystar.antisihr.domain.usecase.MainUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainUseCaseImpl @Inject constructor(
    val repo: MainRepository
) : MainUseCase {
    override suspend fun addFeedback(feedbackData: AddFeedbackData) = repo.addFeedback(feedbackData)
    override suspend fun getListOfLanguages() = repo.getListOfLanguages()
    override suspend fun getInfoAboutApp()= repo.getInfoAboutApp()
}
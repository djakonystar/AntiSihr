package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.data.models.TestResultData
import dev.djakonystar.antisihr.domain.repository.AudioRepository
import dev.djakonystar.antisihr.domain.repository.TestRepository
import dev.djakonystar.antisihr.domain.usecase.AudioUseCase
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AudioUseCaseImpl @Inject constructor(
    private val repo: AudioRepository
) : AudioUseCase {

    override suspend fun getListOfAudios() = repo.getListOfAudios()
}
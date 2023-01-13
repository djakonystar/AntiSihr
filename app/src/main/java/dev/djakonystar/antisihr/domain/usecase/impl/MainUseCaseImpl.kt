package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.domain.repository.MainRepository
import dev.djakonystar.antisihr.domain.usecase.MainUseCase
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainUseCaseImpl @Inject constructor(
    private val repo: MainRepository
) : MainUseCase {}
package dev.djakonystar.antisihr.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.domain.repository.MainRepository
import dev.djakonystar.antisihr.domain.repository.impl.MainRepositoryImpl
import dev.djakonystar.antisihr.domain.usecase.MainUseCase
import dev.djakonystar.antisihr.domain.usecase.impl.MainUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun bindAuthRepository(impl: MainUseCaseImpl): MainUseCase

}
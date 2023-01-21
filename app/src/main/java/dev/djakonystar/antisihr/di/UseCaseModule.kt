package dev.djakonystar.antisihr.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.domain.repository.impl.AudioRepositoryImpl
import dev.djakonystar.antisihr.domain.usecase.*
import dev.djakonystar.antisihr.domain.usecase.impl.*

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun bindTestRepository(impl: TestUseCaseImpl): TestUseCase


    @Binds
    fun bindAudioRepository(impl: AudioUseCaseImpl): AudioUseCase

    @Binds
    fun bindReadersUseCase(impl: ReadersUseCaseImpl): ReadersUseCase

    @Binds
    fun bindLibraryUseCase(impl: LibraryUseCaseImpl): LibraryUseCase

    @Binds
    fun bindMainUseCase(impl: MainUseCaseImpl): MainUseCase
}
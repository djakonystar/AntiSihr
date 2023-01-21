package dev.djakonystar.antisihr.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.domain.repository.impl.AudioRepositoryImpl
import dev.djakonystar.antisihr.domain.usecase.AudioUseCase
import dev.djakonystar.antisihr.domain.usecase.LibraryUseCase
import dev.djakonystar.antisihr.domain.usecase.ReadersUseCase
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import dev.djakonystar.antisihr.domain.usecase.impl.AudioUseCaseImpl
import dev.djakonystar.antisihr.domain.usecase.impl.LibraryUseCaseImpl
import dev.djakonystar.antisihr.domain.usecase.impl.ReadersUseCaseImpl
import dev.djakonystar.antisihr.domain.usecase.impl.TestUseCaseImpl

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
}
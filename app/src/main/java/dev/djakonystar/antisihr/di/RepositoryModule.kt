package dev.djakonystar.antisihr.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.domain.repository.AudioRepository
import dev.djakonystar.antisihr.domain.repository.LibraryRepository
import dev.djakonystar.antisihr.domain.repository.ReadersRepository
import dev.djakonystar.antisihr.domain.repository.TestRepository
import dev.djakonystar.antisihr.domain.repository.impl.AudioRepositoryImpl
import dev.djakonystar.antisihr.domain.repository.impl.LibraryRepositoryImpl
import dev.djakonystar.antisihr.domain.repository.impl.ReadersRepositoryImpl
import dev.djakonystar.antisihr.domain.repository.impl.TestRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindTestRepository(impl: TestRepositoryImpl): TestRepository


    @Binds
    fun bindAudioRepository(impl: AudioRepositoryImpl): AudioRepository

    @Binds
    fun bindReadersRepository(impl: ReadersRepositoryImpl): ReadersRepository

    @Binds
    fun bindLibraryRepository(impl: LibraryRepositoryImpl): LibraryRepository

}
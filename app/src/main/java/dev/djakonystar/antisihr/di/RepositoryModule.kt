package dev.djakonystar.antisihr.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.domain.repository.*
import dev.djakonystar.antisihr.domain.repository.impl.*

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


    @Binds
    fun bindMainRepository(impl: MainRepositoryImpl): MainRepository

}
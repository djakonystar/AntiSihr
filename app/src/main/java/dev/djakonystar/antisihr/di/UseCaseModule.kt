package dev.djakonystar.antisihr.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import dev.djakonystar.antisihr.domain.usecase.impl.TestUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun bindAuthRepository(impl: TestUseCaseImpl): TestUseCase

}
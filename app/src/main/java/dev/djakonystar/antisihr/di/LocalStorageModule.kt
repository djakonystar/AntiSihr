package dev.djakonystar.antisihr.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.data.local.LocalStorage

@Module
@InstallIn(SingletonComponent::class)
class LocalStorageModule {

    @Provides
    fun localStorageProvides(@ApplicationContext context: Context): LocalStorage =
        LocalStorage(context)
}
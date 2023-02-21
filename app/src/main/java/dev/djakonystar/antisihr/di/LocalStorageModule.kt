package dev.djakonystar.antisihr.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.data.local.LocalStorage
import dev.djakonystar.antisihr.data.room.LocalRoomDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalStorageModule {

    @Provides
    fun localStorageProvides(@ApplicationContext context: Context): LocalStorage =
        LocalStorage(context)


    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): LocalRoomDatabase {
        return Room.databaseBuilder(
            context, LocalRoomDatabase::class.java, LocalRoomDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }
}
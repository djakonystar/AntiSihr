package dev.djakonystar.antisihr.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.djakonystar.antisihr.data.room.dao.ArticlesDao
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked


@Database(entities = [ArticlesBookmarked::class], version = 1)
abstract class LocalRoomDatabase: RoomDatabase() {
    abstract fun articleDao(): ArticlesDao

    companion object{
        const val DATABASE_NAME = "articles_db"
    }
}
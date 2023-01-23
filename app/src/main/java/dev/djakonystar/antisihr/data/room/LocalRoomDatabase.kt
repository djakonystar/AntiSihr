package dev.djakonystar.antisihr.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.djakonystar.antisihr.data.room.dao.ArticlesDao
import dev.djakonystar.antisihr.data.room.dao.ShopsDao
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked


@Database(entities = [ArticlesBookmarked::class, ShopItemBookmarked::class], version = 2)
abstract class LocalRoomDatabase: RoomDatabase() {
    abstract fun articleDao(): ArticlesDao
    abstract fun goodsDao(): ShopsDao

    companion object{
        const val DATABASE_NAME = "antisihr_db"
    }
}
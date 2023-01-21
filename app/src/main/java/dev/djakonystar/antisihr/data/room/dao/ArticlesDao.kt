package dev.djakonystar.antisihr.data.room.dao

import androidx.room.Dao


@Dao
interface ArticlesDao {

    suspend fun getFavouriteArticles()
}
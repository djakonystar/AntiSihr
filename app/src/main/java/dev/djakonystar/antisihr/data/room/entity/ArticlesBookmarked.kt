package dev.djakonystar.antisihr.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bookmarked_articles")
data class ArticlesBookmarked(
    @PrimaryKey(autoGenerate = false) val id: Int, val lead: String, val title: String
)
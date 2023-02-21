package dev.djakonystar.antisihr.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "audio_articles")
data class AudioBookmarked(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val author: String,
    val date_create: String?,
    val date_update: String?,
    val image: String,
    val name: String,
    val url: String
)
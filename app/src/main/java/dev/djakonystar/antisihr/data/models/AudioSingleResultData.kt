package dev.djakonystar.antisihr.data.models

import androidx.annotation.Keep

@Keep
data class AudioSingleResultData(
    val author: String,
    val date_create: String,
    val date_update: String,
    val lang: String,
    val id: Int,
    val image: String,
    val name: String,
    val url: String
)
package dev.djakonystar.antisihr.data.models

data class AudioSingleData(
    val error: Boolean,
    val message: String,
    val result: List<AudioSingleResultData>
)
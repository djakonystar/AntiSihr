package dev.djakonystar.antisihr.data.models

data class ListOfAudiosData(
    val error: Boolean,
    val message: String,
    val result: List<AudioResultData>
)
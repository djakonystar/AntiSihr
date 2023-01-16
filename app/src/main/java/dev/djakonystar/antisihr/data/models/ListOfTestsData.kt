package dev.djakonystar.antisihr.data.models

data class ListOfTestsData(
    val error: Boolean,
    val message: String,
    val result: List<ListOfTestsResultData>
)
package dev.djakonystar.antisihr.data.models

data class TestResultData(
    val error: Boolean,
    val message: String,
    val result: List<TestInnerResultData>
)
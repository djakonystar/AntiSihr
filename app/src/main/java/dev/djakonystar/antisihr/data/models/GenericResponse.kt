package dev.djakonystar.antisihr.data.models

data class GenericResponse<T>(
    val error: Boolean,
    val message: String,
    val result: T? = null
)

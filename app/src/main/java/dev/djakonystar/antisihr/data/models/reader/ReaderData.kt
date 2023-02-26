package dev.djakonystar.antisihr.data.models.reader

data class ReaderData(
    val id: Int,
    val city: CityData?,
    val name: String,
    val image: String,
    val surname: String,
    val description: String
)

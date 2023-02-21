package dev.djakonystar.antisihr.data.models

data class CityData (
    val error:Boolean,
    val message:String,
    val result: CityInnerData
        )
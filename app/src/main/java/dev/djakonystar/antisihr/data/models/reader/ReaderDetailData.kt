package dev.djakonystar.antisihr.data.models.reader

import com.google.gson.annotations.SerializedName

data class ReaderDetailData(
    val id: Int,
    val surname: String,
    val name: String,
    val description: String,
    val image: String,
    val address: String?,
    val phone: String,
    val city: CityData?,
    @SerializedName("social_networks") val socialNetworks: List<SocialNetwork>
)

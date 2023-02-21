package dev.djakonystar.antisihr.data.models.library

data class InnerLibraryBookmarkData(
    val id: Int, val lead: String, val title: String, var isBookmarked: Boolean
)
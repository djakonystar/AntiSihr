package dev.djakonystar.antisihr.presentation.library

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import kotlinx.coroutines.flow.Flow

interface LibraryScreenViewModel {
    val getListOfSectionsLibraryFlow:Flow<GenericResponse<List<LibraryResultData>>>
    val messageFlow:Flow<String>
    val errorFlow:Flow<Throwable>

    suspend fun getListOfSectionsLibrary()
}
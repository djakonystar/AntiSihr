package dev.djakonystar.antisihr.domain.repository.impl

import dev.djakonystar.antisihr.data.models.ListOfAudiosData
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import dev.djakonystar.antisihr.data.room.LocalRoomDatabase
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.domain.repository.AudioRepository
import dev.djakonystar.antisihr.domain.repository.TestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AudioRepositoryImpl @Inject constructor(
    private val antiSihrApi: AntiSihrApi,
    private val db: LocalRoomDatabase
) : AudioRepository {

    override suspend fun getListOfAudios() = flow {
        val request = antiSihrApi.getListOfAudios()
        if (request.isSuccessful) {
            emit(ResultData.Success(request.body()!!))
        } else {
            emit(ResultData.Message(request.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getListOfBookmarkedAudios()=flow{
        emit(ResultData.Success(db.audiosDao().getBookmarkedAudio()))
    }

    override suspend fun addAudioToBookmarked(item: AudioBookmarked) = flow {
        emit(ResultData.Success(db.audiosDao().insertAudioToBookmarks(item)))
    }

    override suspend fun deleteAudioFromBookmarked(item: AudioBookmarked) = flow {
        emit(ResultData.Success(db.audiosDao().deleteAudioFromBookmarks(item)))
    }

    override suspend fun isExistsInBookmarkeds(item: AudioBookmarked) = db.audiosDao().isExistsInBookmarkeds(item.id,item.author,item.name)


}
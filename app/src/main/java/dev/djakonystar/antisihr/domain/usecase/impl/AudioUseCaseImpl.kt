package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.data.models.TestResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryBookmarkData
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.domain.repository.AudioRepository
import dev.djakonystar.antisihr.domain.repository.TestRepository
import dev.djakonystar.antisihr.domain.usecase.AudioUseCase
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AudioUseCaseImpl @Inject constructor(
    private val repo: AudioRepository
) : AudioUseCase {

    override suspend fun getListOfAudios() =flow{
        repo.getListOfAudios().collect{
            if (it is ResultData.Success){
                val list = mutableListOf<AudioBookmarked>()
                it.data.result.forEach {
                    list.add(
                        AudioBookmarked(
                            it.id,
                            it.author,
                            it.date_create,
                            it.date_update,
                            it.image,
                            it.name,
                            it.url
                        )
                    )
                }
                emit(ResultData.Success(list))
            }
        }
    }
    override suspend fun getListOfBookmarkedAudios() = repo.getListOfBookmarkedAudios()

    override suspend fun addAudioToBookmarked(item: AudioBookmarked) = repo.addAudioToBookmarked(item)

    override suspend fun deleteAudioFromBookmarked(item: AudioBookmarked)= repo.deleteAudioFromBookmarked(item)
    override suspend fun isExistsInBookmarkeds(item: AudioBookmarked) = repo.isExistsInBookmarkeds(item)
}
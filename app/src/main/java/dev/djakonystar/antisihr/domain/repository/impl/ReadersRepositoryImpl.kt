package dev.djakonystar.antisihr.domain.repository.impl

import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import dev.djakonystar.antisihr.domain.repository.ReadersRepository
import dev.djakonystar.antisihr.utils.errorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadersRepositoryImpl @Inject constructor(
    private val api: AntiSihrApi
): ReadersRepository {

    override suspend fun getReaders() = flow {
        val response = api.getReaders()
        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getReaderById(id: Int) = flow {
        val response = api.getReaderById(id)
        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)
}

package dev.djakonystar.antisihr.domain.repository.impl

import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import dev.djakonystar.antisihr.domain.repository.TestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TestRepositoryImpl @Inject constructor(
    private val antiSihrApi: AntiSihrApi
) : TestRepository {


    override suspend fun getListOfTests() = flow {
        val request = antiSihrApi.getListOfTests()
        if (request.isSuccessful) {
            emit(ResultData.Success(request.body()!!))
        } else {
            emit(ResultData.Message(request.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getResultForTest(id: Int, positive: Int) = flow {
        val request = antiSihrApi.getResultForTest(id, positive)
        if (request.isSuccessful) {
            emit(ResultData.Success(request.body()!!))
        } else {
            emit(ResultData.Message(request.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getTest(id: Int) = flow {
        val request = antiSihrApi.getTest(id)
        if (request.isSuccessful) {
            emit(ResultData.Success(request.body()!!))
        } else {
            emit(ResultData.Message(request.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)


}
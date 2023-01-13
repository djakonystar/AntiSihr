package dev.djakonystar.antisihr.domain.repository.impl

import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import dev.djakonystar.antisihr.domain.repository.MainRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainRepositoryImpl @Inject constructor(
    private val antiSihrApi: AntiSihrApi
) : MainRepository {

}
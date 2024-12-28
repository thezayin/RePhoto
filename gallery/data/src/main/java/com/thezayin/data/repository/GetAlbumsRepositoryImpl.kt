package com.thezayin.data.repository

import com.thezayin.data.network.MediaStoreUtil
import com.thezayin.domain.model.Album
import com.thezayin.domain.repository.GetAlbumsRepository
import com.thezayin.framework.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAlbumsRepositoryImpl(private val mediaStoreUtil: MediaStoreUtil) : GetAlbumsRepository {
    override fun getAlbums(): Flow<Response<List<Album>>> = flow {
        emit(Response.Loading)
        try {
            val albums = mediaStoreUtil.fetchAlbums()
            emit(Response.Success(albums))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Failed to load albums"))
        }
    }
}

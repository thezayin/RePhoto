package com.thezayin.domain.repository

import com.thezayin.domain.model.Album
import com.thezayin.framework.utils.Response
import kotlinx.coroutines.flow.Flow

interface GetAlbumsRepository {
    fun getAlbums(): Flow<Response<List<Album>>>
}
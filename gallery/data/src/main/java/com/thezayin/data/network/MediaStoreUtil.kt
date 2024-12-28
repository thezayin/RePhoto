package com.thezayin.data.network

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.thezayin.domain.model.Album
import com.thezayin.domain.model.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreUtil(private val context: Context) {

    suspend fun fetchAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            val seenBuckets = mutableSetOf<String>()

            while (it.moveToNext()) {
                val bucketId = it.getString(bucketIdColumn)
                if (seenBuckets.contains(bucketId)) continue
                seenBuckets.add(bucketId)

                val bucketName = it.getString(bucketNameColumn)
                val imageId = it.getLong(idColumn)
                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                ).toString()

                albums.add(
                    Album(
                        id = bucketId,
                        name = bucketName,
                        coverImageUri = imageUri
                    )
                )
            }
        }

        albums
    }

    suspend fun fetchImages(albumId: String): List<Image> = withContext(Dispatchers.IO) {
        val images = mutableListOf<Image>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(albumId)

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (it.moveToNext()) {
                val imageId = it.getLong(idColumn)
                val title = it.getString(nameColumn)
                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )

                images.add(
                    Image(
                        id = imageId.toString(),
                        uri = imageUri,
                        title = title
                    )
                )
            }
        }
        images
    }
}

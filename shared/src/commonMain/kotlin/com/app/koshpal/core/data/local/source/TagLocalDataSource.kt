package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.TagEntity
import kotlinx.coroutines.flow.Flow

interface TagLocalDataSource {
    fun getAllTags(): Flow<List<TagEntity>>
    suspend fun getTagById(id: String): TagEntity?
    suspend fun insertTag(tag: TagEntity)
    suspend fun updateTag(tag: TagEntity)
    suspend fun deleteTag(tag: TagEntity)
    suspend fun deleteTagsByIds(ids: List<String>)
}

package com.app.koshpal.app.domain.repository

import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.domain.util.CallError
import kotlinx.coroutines.flow.Flow

interface TagRepo {
    fun getAllTags(): Flow<List<Tag>>
    suspend fun getTagById(id: String): Result<Tag?, CallError>
    suspend fun createTag(tag: Tag): Result<Unit, CallError>
    suspend fun updateTag(tag: Tag): Result<Unit, CallError>
    suspend fun deleteTag(tag: Tag): Result<Unit, CallError>
    suspend fun deleteTagsByIds(ids: List<String>): Result<Unit, CallError>
}

package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.mapper.toTag
import com.app.koshpal.app.data.mapper.toTagEntity
import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.app.domain.repository.TagRepo
import com.app.koshpal.core.data.local.source.TagLocalDataSource
import com.app.koshpal.core.data.networking.safeDatabaseCall
import com.app.koshpal.core.domain.util.CallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepoImpl(
    private val localDataSource: TagLocalDataSource
) : TagRepo {
    override fun getAllTags(): Flow<List<Tag>> = localDataSource.getAllTags().map { entities ->
        entities.map { it.toTag() }
    }

    override suspend fun getTagById(id: String): Result<Tag?, CallError> = safeDatabaseCall {
        localDataSource.getTagById(id)?.toTag()
    }

    override suspend fun createTag(tag: Tag): Result<Unit, CallError> = safeDatabaseCall {
        localDataSource.insertTag(tag.toTagEntity())
    }

    override suspend fun updateTag(tag: Tag): Result<Unit, CallError> = safeDatabaseCall {
        localDataSource.updateTag(tag.toTagEntity())
    }

    override suspend fun deleteTag(tag: Tag): Result<Unit, CallError> = safeDatabaseCall {
        localDataSource.deleteTag(tag.toTagEntity())
    }

    override suspend fun deleteTagsByIds(ids: List<String>): Result<Unit, CallError> = safeDatabaseCall {
        localDataSource.deleteTagsByIds(ids)
    }
}

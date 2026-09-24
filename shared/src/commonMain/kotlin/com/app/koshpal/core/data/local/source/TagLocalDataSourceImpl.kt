package com.app.koshpal.core.data.local.source

import com.app.koshpal.core.data.entities.TagEntity
import com.app.koshpal.core.data.local.dao.TagDao
import kotlinx.coroutines.flow.Flow

class TagLocalDataSourceImpl(
    private val tagDao: TagDao
) : TagLocalDataSource {
    override fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()
    override suspend fun getTagById(id: String): TagEntity? = tagDao.getTagById(id)
    override suspend fun insertTag(tag: TagEntity) = tagDao.insertTag(tag)
    override suspend fun updateTag(tag: TagEntity) = tagDao.updateTag(tag)
    override suspend fun deleteTag(tag: TagEntity) = tagDao.deleteTag(tag)
    override suspend fun deleteTagsByIds(ids: List<String>) = tagDao.deleteTagsByIds(ids)
}

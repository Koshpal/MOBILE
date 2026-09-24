package com.app.koshpal.app.domain.usecase.tagusecase

import com.app.koshpal.app.domain.model.Tag
import com.app.koshpal.app.domain.repository.TagRepo
import com.app.koshpal.core.domain.util.CallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

data class TagUseCases(
    val getAllTags: GetAllTagsUseCase,
    val getTagById: GetTagByIdUseCase,
    val createTag: CreateTagUseCase,
    val updateTag: UpdateTagUseCase,
    val deleteTag: DeleteTagUseCase,
    val deleteTagsByIds: DeleteTagsByIdsUseCase
)

class GetAllTagsUseCase(private val repository: TagRepo) {
    operator fun invoke(): Flow<List<Tag>> = repository.getAllTags()
}

class GetTagByIdUseCase(private val repository: TagRepo) {
    suspend operator fun invoke(id: String): Result<Tag?, CallError> = repository.getTagById(id)
}

class CreateTagUseCase(private val repository: TagRepo) {
    suspend operator fun invoke(tag: Tag): Result<Unit, CallError> = repository.createTag(tag)
}

class UpdateTagUseCase(private val repository: TagRepo) {
    suspend operator fun invoke(tag: Tag): Result<Unit, CallError> = repository.updateTag(tag)
}

class DeleteTagUseCase(private val repository: TagRepo) {
    suspend operator fun invoke(tag: Tag): Result<Unit, CallError> = repository.deleteTag(tag)
}

class DeleteTagsByIdsUseCase(private val repository: TagRepo) {
    suspend operator fun invoke(ids: List<String>): Result<Unit, CallError> = repository.deleteTagsByIds(ids)
}

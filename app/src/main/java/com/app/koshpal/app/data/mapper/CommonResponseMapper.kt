package com.app.koshpal.app.data.mapper

import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.core.data.remote.dto.CommonResponseDto

fun CommonResponseDto.toCommonResponse(): CommonResponse {
    return CommonResponse(
        status = this.status,
        message = this.message
    )
}
package org.zapply.product.domain.posting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.zapply.product.global.clova.enuermerate.SNSType;

public record ToneTransferRequest(
        @NotNull
        @Schema(description = "변환할 SNS 타입", example = "INSTAGRAM")
        SNSType snsType,

        @NotNull
        @Schema(description = "사용자 입력 콘텐츠", example = "오늘 점심 뭐 먹을지 고민이야.")
        String userPrompt
) {
}

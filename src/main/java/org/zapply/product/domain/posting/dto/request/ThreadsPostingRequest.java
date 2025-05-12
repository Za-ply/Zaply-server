package org.zapply.product.domain.posting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public record ThreadsPostingRequest(
        @NotNull
        @Schema(description = "미디어 타입", example = "IMAGE || VIDEO || TEXT")
        String mediaType,

        @NotNull
        @Schema(description = "미디어 url", example = "https://zaply-landing.vercel.app/assets/images/ZaplyLanding.webp")
        String media,

        @NotNull
        @Schema(description = "미디어 텍스트", example = "미디어 텍스트")
        @Max(value = 500, message = "미디어 텍스트는 500자 이내로 입력해주세요")
        String text
) {
}

package org.zapply.product.global.snsClients.instagram;

import io.swagger.v3.oas.annotations.media.Schema;

public record InstagramPostingRequest(
        @Schema(description = "미디어 url", example = "https://zaply-landing.vercel.app/assets/images/ZaplyLanding.webp")
        String imageUrl,
        String caption
) {
}

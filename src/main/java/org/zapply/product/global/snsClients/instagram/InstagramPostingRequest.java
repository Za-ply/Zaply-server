package org.zapply.product.global.snsClients.instagram;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record InstagramPostingRequest(
        @Schema(description = "미디어 url",
                example = "[\"https://zaply-landing.vercel.app/assets/images/ZaplyLanding.webp\", " +
                        "\"https://zaply-landing.vercel.app/assets/images/ZaplyLanding.webp\"]")
        List<String> mediaUrls,
        String caption
) {
}

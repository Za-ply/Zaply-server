package org.zapply.product.global.snsClients.instagram;

import lombok.Builder;

import java.util.List;

@Builder
public record InstagramMediaResponse(
        String id,
        String caption,
        String mediaType,
        List<String> mediaUrls,
        String permalink,
        String timestamp
) {}

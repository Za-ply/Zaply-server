package org.zapply.product.global.clova.dto.response;

public record ClovaResponse(
        ClovaStatus status,
        ClovaResult result
) {}

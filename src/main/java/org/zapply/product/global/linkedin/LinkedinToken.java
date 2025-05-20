package org.zapply.product.global.linkedin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LinkedinToken(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Long expiresIn
) {}
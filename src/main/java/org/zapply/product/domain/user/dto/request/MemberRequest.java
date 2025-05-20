package org.zapply.product.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberRequest(
        String name,
        String password
) {
}

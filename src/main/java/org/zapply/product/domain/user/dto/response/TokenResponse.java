package org.zapply.product.domain.user.dto.response;
import lombok.Builder;

@Builder
public record TokenResponse(
        Long memberId,
        String accessToken,
        String refreshToken
) {
    public static TokenResponse of(Long memberId, String accessToken, String refreshToken) {
        return TokenResponse.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

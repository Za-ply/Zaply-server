package org.zapply.product.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.zapply.product.domain.user.entity.Credential;
import org.zapply.product.domain.user.entity.Member;

@Builder
public record AuthRequest(

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "이메일", example = "zaply123@gmail.com")
        String email,

        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "주민등록번호", example = "123456-1234567")
        String residentNumber,

        @Schema(description = "비밀번호", example = "password123")
        String password
) {
    public Member toMember(Credential credential) {
        return Member.builder()
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .residentNumber(residentNumber)
                .credential(credential)
                .build();
    }
}

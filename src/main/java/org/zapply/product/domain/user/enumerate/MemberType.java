package org.zapply.product.domain.user.enumerate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    GENERAL("일반 사용자"),
    ADMIN("관리자");

    private final String role;
}
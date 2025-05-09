package org.zapply.product.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SNSType {
    INSTAGRAM("인스타그램"),
    FACEBOOK("페이스북"),
    THREADS("쓰레드");

    private final String description;
}
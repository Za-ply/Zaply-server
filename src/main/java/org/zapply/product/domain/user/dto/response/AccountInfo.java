package org.zapply.product.domain.user.dto.response;

import lombok.Builder;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.global.clova.enuermerate.SNSType;


@Builder
public record AccountInfo(
        SNSType snsType,
        String accountName
) {
    public static AccountInfo of(Account account) {
        return AccountInfo.builder()
                .snsType(account.getAccountType())
                .accountName(account.getAccountName())
                .build();
    }
}

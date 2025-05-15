package org.zapply.product.domain.user.dto.response;

import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.enumerate.SNSType;

public record AccountInfo (
        SNSType snsType,
        String accountName
){
    public static AccountInfo of(Account account) {
        return new AccountInfo(
                account.getAccountType(),
                account.getAccountName()
        );
    }
}

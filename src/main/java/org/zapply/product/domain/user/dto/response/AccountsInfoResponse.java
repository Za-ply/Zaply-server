package org.zapply.product.domain.user.dto.response;

import java.util.List;

public record AccountsInfoResponse(
        int totalCount,
        List<AccountInfo> accounts
) {
    public static AccountsInfoResponse of(List<AccountInfo> accountInfos) {
        return new AccountsInfoResponse(
                accountInfos.size(),
                accountInfos
        );
    }
}
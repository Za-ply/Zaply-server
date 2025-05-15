package org.zapply.product.domain.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zapply.product.domain.user.dto.response.AccountsInfoResponse;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.enumerate.SNSType;
import org.zapply.product.domain.user.repository.AccountRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAccountsInfo_returnsWrappedDto() {
        // given
        Member member = Member.builder()
                .email("test@gmail.com")
                .name("테스트유저")
                .build();

        Account acc = Account.builder()
                .accountType(SNSType.FACEBOOK)
                .accountName("fb_test")
                .email("fb@facebook.com")
                .tokenKey("vault_key")
                .member(member)
                .tokenExpireAt(LocalDateTime.now().plusDays(1))
                .userId("uid")
                .build();

        when(accountRepository.findAllByMember(member))
                .thenReturn(List.of(acc));

        // when
        AccountsInfoResponse accountsInfoResponse = accountService.getAccountsInfo(member);

        // then
        assertEquals(1, accountsInfoResponse.totalCount());
        assertEquals(1, accountsInfoResponse.accounts().size());
        assertEquals(SNSType.FACEBOOK, accountsInfoResponse.accounts().getFirst().snsType());
        assertEquals("fb_test", accountsInfoResponse.accounts().getFirst().accountName());

        verify(accountRepository).findAllByMember(member);
    }
}
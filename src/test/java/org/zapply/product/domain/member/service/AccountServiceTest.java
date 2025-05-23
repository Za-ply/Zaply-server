package org.zapply.product.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zapply.product.domain.user.dto.response.AccountsInfoResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.repository.AccountRepository;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.vault.VaultClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private VaultClient vaultClient;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        // if you want to assert on which path gets passed to VaultClient,
        // set the facebookPath/threadsPath fields manually:
        ReflectionTestUtils.setField(accountService, "facebookPath", "facebook/clients");
        ReflectionTestUtils.setField(accountService, "threadsPath",  "threads/clients");
    }

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

    @Test
    @DisplayName("unlinkService: 계정이 있으면 VaultClient.deleteSecretKey() 와 repository.delete() 가 호출된다")
    void unlinkService_success() {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .name("테스트유저")
                .build();

        Account account = Account.builder()
                .accountType(SNSType.FACEBOOK)
                .accountName("fb_test")
                .email("fb@test.com")
                .tokenKey("key")  // <— this is "key"
                .member(member)
                .tokenExpireAt(LocalDateTime.now().plusDays(1))
                .userId("uid")
                .build();

        when(accountRepository.findByAccountTypeAndMember(SNSType.FACEBOOK, member))
                .thenReturn(Optional.of(account));

        // when / then
        assertDoesNotThrow(() ->
                accountService.unlinkService(SNSType.FACEBOOK, member)
        );

        // now verify with the exact same values
        verify(vaultClient, times(1))
                .deleteSecretKey("facebook/clients", "key");

        verify(accountRepository, times(1))
                .delete(account);
    }


    @Test
    @DisplayName("unlinkService: 계정이 없으면 CoreException(ACCOUNT_NOT_FOUND) 발생")
    void unlinkService_notFound_throws() {
        // given
        Member member = Member.builder()
                .email("none@example.com")
                .name("없음")
                .build();

        when(accountRepository.findByAccountTypeAndMember(SNSType.THREADS, member))
                .thenReturn(Optional.empty());

        // when & then
        CoreException ex = assertThrows(CoreException.class, () ->
                accountService.unlinkService(SNSType.THREADS, member)
        );
        assertEquals(GlobalErrorType.ACCOUNT_NOT_FOUND, ex.getErrorType());

        verify(vaultClient, never()).deleteSecretKey(any(), any());
        verify(accountRepository, never()).delete(any());
    }
}
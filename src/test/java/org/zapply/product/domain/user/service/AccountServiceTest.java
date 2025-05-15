package org.zapply.product.domain.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.enumerate.SNSType;
import org.zapply.product.domain.user.repository.AccountRepository;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("unlinkService: 계정이 있으면 delete 메서드가 호출된다")
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
                .tokenKey("key")
                .member(member)
                .tokenExpireAt(LocalDateTime.now().plusDays(1))
                .userId("uid")
                .build();

        when(accountRepository.findByAccountTypeAndMember(SNSType.FACEBOOK, member))
                .thenReturn(Optional.of(account));

        // when
        assertDoesNotThrow(() ->
                accountService.unlinkService(SNSType.FACEBOOK, member)
        );

        // then
        verify(accountRepository, times(1))
                .findByAccountTypeAndMember(SNSType.FACEBOOK, member);
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

        verify(accountRepository, times(1))
                .findByAccountTypeAndMember(SNSType.THREADS, member);
        verify(accountRepository, never()).delete(any());
    }
}
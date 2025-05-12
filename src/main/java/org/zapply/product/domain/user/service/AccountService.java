package org.zapply.product.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.enumerate.SNSType;
import org.zapply.product.domain.user.repository.AccountRepository;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.facebook.FacebookClient;
import org.zapply.product.global.facebook.FacebookProfile;
import org.zapply.product.global.facebook.FacebookToken;
import org.zapply.product.global.threads.ThreadsClient;
import org.zapply.product.global.threads.ThreadsProfile;
import org.zapply.product.global.threads.ThreadsToken;
import org.zapply.product.global.vault.VaultClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final FacebookClient facebookClient;
    private final ThreadsClient threadsClient;
    private final VaultClient vaultClient;

    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    private String facebookRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.threads.redirect-uri}")
    private String threadsRedirectUrl;

    @Value("${spring.cloud.vault.facebook-path}")
    private String facebookPath;

    @Value("${spring.cloud.vault.threads-path}")
    private String threadsPath;


    /**
     * 페이스북 계정 연동
     *
     * @param code
     * @param member
     * @return Redis Key
     */
    public String linkFacebook(String code, Member member) {
        // 페이스북으로 액세스 토큰 요청하기
        FacebookToken shortFacebookAccessToken = facebookClient.getFacebookAccessToken(code, facebookRedirectUrl);
        FacebookToken longFacebookAccessToken = facebookClient.getLongLivedToken(shortFacebookAccessToken.accessToken());

        // 페이스북에 있는 사용자 정보 반환
        FacebookProfile facebookProfile = facebookClient.getMemberInfo(longFacebookAccessToken);

        // 반환된 정보의 이메일 추출
        String email = facebookProfile.email();
        if (email == null) {
            throw new CoreException(GlobalErrorType.EMAIL_NOT_FOUND);
        }

        // key 생성
        String key = "facebook:" + "client:" + generateKey(member.getId(), email);

        // business logic: account 정보가 이미 있다면 확인 후 해당 account 정보를 반환하고, 없다면 새로운 account 정보를 생성하여 반환
        Account account = accountRepository.findByEmailAndAccountTypeAndMember(email, SNSType.FACEBOOK, member)
                .map(existingAccount -> {
                    // 기존 계정이 있다면 Vault에서 토큰을 갱신해준다.
                    vaultClient.saveSecret(facebookPath, key, longFacebookAccessToken.accessToken());
                    // 토큰 만료일 갱신
                    existingAccount.updateTokenExpireAt(LocalDateTime.now().plusDays(60));
                    return accountRepository.save(existingAccount);
                })
                .orElseGet(() -> {
                    // 계정이 없다면 새 계정 생성
                    Account newAccount = Account.builder()
                            .accountName(facebookProfile.name())
                            .email(email)
                            .accountType(SNSType.FACEBOOK)
                            .tokenKey(key)
                            .member(member)
                            .tokenExpireAt(LocalDateTime.now().plusDays(60))
                            .userId(facebookProfile.id())
                            .build();
                    Account savedAccount = accountRepository.save(newAccount);
                    // 새 계정을 Vault에 저장
                    vaultClient.saveSecret(facebookPath, key, longFacebookAccessToken.accessToken());
                    return savedAccount; // 새 계정 반환
                });

        return key;
    }

    /**
     * 스레드 계정 연동
     *
     * @param code
     * @param member
     * @return Redis Key
     */
    public String linkThreads(String code, Member member) {
        // 스레드로 액세스 토큰 요청하기
        ThreadsToken shortThreadsToken = threadsClient.getThreadsAccessToken(code, threadsRedirectUrl);

        // 스레드에서 장기 액세스 토큰 요청하기
        ThreadsToken longThreadsToken = threadsClient.getLongLivedToken(shortThreadsToken.accessToken());

        // 스레드에 있는 사용자 정보 반환
        ThreadsProfile profile = threadsClient.getThreadsProfile(longThreadsToken.accessToken());

        // 반환된 정보의 username 추출, key 생성
        String key = "threads:" + "client:" + generateKey(member.getId(), profile.username());

        //bussiness logic: account 정보가 이미 있다면 확인 후 해당 account 정보를 반환하고, 없다면 새로운 account 정보를 생성하여 반환
        Account account = accountRepository.findByAccountNameAndAccountTypeAndMember(profile.username(), SNSType.THREADS, member)
                .map(existingAccount -> {
                    // 기존 계정이 있다면 Vault에서 토큰을 갱신해준다.
                    vaultClient.saveSecret(threadsPath, key, longThreadsToken.accessToken());
                    // 토큰 만료일 갱신
                    existingAccount.updateTokenExpireAt(LocalDateTime.now().plusDays(60));
                    return accountRepository.save(existingAccount);
                })
                .orElseGet(() -> {
                    // 계정이 없다면 새로 저장
                    Account newAccount = Account.builder()
                            .accountName(profile.username())
                            .email("")
                            .accountType(SNSType.THREADS)
                            .tokenKey(key)
                            .member(member)
                            .tokenExpireAt(LocalDateTime.now().plusDays(60))
                            .userId(profile.id())
                            .build();
                    Account savedAccount = accountRepository.save(newAccount);
                    // Vault에 토큰 저장
                    vaultClient.saveSecret(threadsPath, key, longThreadsToken.accessToken());
                    // 토큰 만료일 갱신
                    return savedAccount;
                });

        return key;
    }

    /**
     * Key 생성
     *
     * @param memberId
     * @param email
     * @return Key
     */
    public String generateKey(Long memberId, String email) {
        try {
            String rawKey = memberId + ":" + email;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes());
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CoreException(GlobalErrorType.SHA256_GENERATION_ERROR);
        }
    }

    /**
     * 사용자의 Vault에 저장된 액세스 토큰을 가져옴
     *
     * @param member
     * @param accountType
     * @return 액세스 토큰
     */
    public String getAccessToken(Member member, SNSType accountType) {
        Account account = accountRepository.findByAccountTypeAndMember(accountType, member)
                .orElseThrow(() -> new CoreException(GlobalErrorType.ACCOUNT_TOKEN_KEY_NOT_FOUND));

        String vaultPath;
        switch (accountType) {
            case FACEBOOK -> vaultPath = facebookPath;
            case THREADS -> vaultPath = threadsPath;
            default -> throw new CoreException(GlobalErrorType.SNS_TYPE_NOT_FOUND);
        }

        // 토큰 만료일 체크
        if (isTokenExpired(account)) {
            throw new CoreException(GlobalErrorType.TOKEN_INVALID);
        }

        // Vault에서 액세스 토큰 가져오기
        String accessToken = vaultClient.getSecret(vaultPath, account.getTokenKey());
        if (accessToken == null || accessToken.isBlank()) {
            throw new CoreException(GlobalErrorType.VAULT_TOKEN_NOT_FOUND);
        }

        return accessToken;
    }

    /**
     * 토큰 갱신 날짜 유효성 체크
     * @param account
     * @return true: 만료됨, false: 만료되지 않음
     */
    public boolean isTokenExpired(Account account) {
        return account.getTokenExpireAt() == null || LocalDateTime.now().isAfter(account.getTokenExpireAt());
    }
}

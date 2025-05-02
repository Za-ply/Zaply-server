package org.zapply.product.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.user.entity.Account;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.enumerate.SNSType;
import org.zapply.product.domain.user.repository.AccountRepository;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.redis.RedisClient;
import org.zapply.product.global.security.facebook.FacebookClient;
import org.zapply.product.global.security.facebook.FacebookProfile;
import org.zapply.product.global.security.facebook.FacebookToken;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RedisClient redisClient;
    private final FacebookClient facebookClient;

    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    private String facebookRedirectUrl;


    /**
     * 페이스북 계정 연동
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

        // rediskey 생성
        String redisKey = "facebook:" + generateRedisKey(member.getId(), email);

        //bussiness logic: account 정보가 이미 있다면 확인 후 해당 account 정보를 반환하고, 없다면 새로운 account 정보를 생성하여 반환
        Account account = accountRepository.findByEmailAndAccountTypeAndMember(email, SNSType.FACEBOOK, member)
                .orElseGet(() -> {
                    Account newAccount = Account.builder()
                            .accountName(facebookProfile.name())
                            .email(email)
                            .accountType(SNSType.FACEBOOK)
                            .tokenKey(redisKey)
                            .member(member)
                            .build();
                    return accountRepository.save(newAccount);
                });

        // Redis에 값 저장 (60일동안 유지)
        redisClient.setValue(redisKey, longFacebookAccessToken.accessToken(), 5184000L);

        return redisKey;
    }

    /**
     * Redis Key 생성
     * @param memberId
     * @param email
     * @return Redis Key
     */
    public String generateRedisKey(Long memberId, String email) {
        try {
            String rawKey = memberId + ":" + email;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes());
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CoreException(GlobalErrorType.SHA256_GENERATION_ERROR);
        }
    }
}

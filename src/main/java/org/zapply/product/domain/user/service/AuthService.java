package org.zapply.product.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.domain.user.dto.request.AuthRequest;
import org.zapply.product.domain.user.dto.request.SignInRequest;
import org.zapply.product.domain.user.dto.response.TokenResponse;
import org.zapply.product.domain.user.dto.response.MemberResponse;
import org.zapply.product.domain.user.entity.Credential;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.repository.MemberRepository;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.redis.RedisClient;
import org.zapply.product.global.security.jwt.JwtProvider;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final CredentialService credentialService;
    private final JwtProvider jwtProvider;
    private final RedisClient redisClient;
    private final MemberRepository memberRepository;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long refreshTokenExpirationTime;

    /**
     * 회원가입 메서드
     * @param authRequest
     * @return MemberResponse
     */
    @Transactional
    public MemberResponse signUp(AuthRequest authRequest) {
        Credential credential = credentialService.createCredential(authRequest);
        Member member = userService.createUser(credential, authRequest);

        return MemberResponse.of(member);
    }

    /**
     * 로그인 메서드
     * @param signInRequest
     * @return TokenResponse
     */
    @Transactional
    public TokenResponse signIn(SignInRequest signInRequest) {
        Member member = userService.getUserByEmail(signInRequest.email());
        credentialService.checkPassword(member, signInRequest.password());

        TokenResponse tokenResponse = jwtProvider.createToken(member);
        redisClient.setValue(member.getEmail(), tokenResponse.refreshToken(), refreshTokenExpirationTime);

        return tokenResponse;
    }

    /**
     * 로그아웃 메서드
     * @param refreshToken
     * @param accessToken
     */
    @Transactional
    public void signOut(String refreshToken, String accessToken) {
        if(refreshToken==null || accessToken==null) throw new CoreException(GlobalErrorType.TOKEN_NOT_FOUND);
        if(!jwtProvider.validateToken(accessToken)) {
            throw new CoreException(GlobalErrorType.TOKEN_INVALID);
        }
        jwtProvider.invalidateTokens(refreshToken, accessToken);
    }

    /**
     * 토큰 재발급 메서드
     * @param token
     * @param member
     * @return tokenResponse
     */
    public TokenResponse recreate(String token, Member member) {
        if(token ==null) throw new CoreException(GlobalErrorType.TOKEN_NOT_FOUND);
        if (member == null) throw new CoreException(GlobalErrorType.MEMBER_NOT_FOUND);

        String refreshToken = token.substring(7);
        boolean isValid = jwtProvider.validateToken(refreshToken);

        if (!isValid) throw new CoreException(GlobalErrorType.TOKEN_INVALID);

        String email = jwtProvider.getEmail(refreshToken);
        String redisRefreshToken = redisClient.getValue(email);

        if (refreshToken.isEmpty() || redisRefreshToken.isEmpty() || !redisRefreshToken.equals(refreshToken)) {
            throw new CoreException(GlobalErrorType.TOKEN_NOT_FOUND);
        }

        return jwtProvider.recreate(member, refreshToken);
    }

    /**
     * 이메일 중복 확인 메서드
     * @param email
     * @return boolean
     */
    public boolean checkEmailDuplicate(String email) {
        if (email == null || email.isEmpty()) {
            throw new CoreException(GlobalErrorType.EMAIL_NOT_FOUND);
        }
        return memberRepository.existsByEmailAndDeletedAtIsNull(email);
    }
}
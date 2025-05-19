package org.zapply.product.global.security.oAuth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.zapply.product.domain.user.dto.response.TokenResponse;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.repository.MemberRepository;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.redis.RedisClient;
import org.zapply.product.global.security.jwt.JwtProvider;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RedisClient redisClient;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshTokenExpirationTime;

    @Value("${jwt.token.access-expiration-time}")
    private long accessTokenExpirationTime;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // 필요한 사용자 정보 추출
            String email = oAuth2User.getEmail();
            Long memberId = oAuth2User.getMemberId();

            // DB에서 회원 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        return new CoreException(GlobalErrorType.MEMBER_NOT_FOUND);
                    });

            // JWT 토큰 생성
            TokenResponse tokenResponse = jwtProvider.createToken(member);

            String refreshToken = tokenResponse.refreshToken();

            // Redis에 refresh token 저장 (키: 이메일)
            redisClient.setValue(email, refreshToken, refreshTokenExpirationTime);

            Cookie accessCookie = new Cookie("accessToken", tokenResponse.accessToken());
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge((int)(accessTokenExpirationTime / 1000));
            response.addCookie(accessCookie);

            Cookie refreshCookie = new Cookie("refreshToken", tokenResponse.refreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge((int)(refreshTokenExpirationTime / 1000));
            response.addCookie(refreshCookie);

            String targetUrl = "http://localhost:3000/google/callback";
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
        catch (IOException e) {
            throw new CoreException(GlobalErrorType.OAUTH_ERROR);
        }
    }
}

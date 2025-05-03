package org.zapply.product.global.security.google.oAuth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.zapply.product.domain.user.dto.response.TokenResponse;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.repository.MemberRepository;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.redis.RedisClient;
import org.zapply.product.global.security.jwt.JwtProvider;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshTokenExpirationTime;

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
                        return new IllegalStateException("Member not found: " + memberId);
                    });

            // JWT 토큰 생성
            TokenResponse tokenResponse = jwtProvider.createToken(member);

            String refreshToken = tokenResponse.refreshToken();

            // Redis에 refresh token 저장 (키: 이메일)
            redisClient.setValue(email, refreshToken, refreshTokenExpirationTime);

            // ApiResponse 래핑
            ApiResponse<TokenResponse> apiResponse = ApiResponse.success(tokenResponse);

            // JSON으로 쓰기
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        }
        catch (IOException e) {
            throw new CoreException(GlobalErrorType.OAUTH_ERROR);
        }
    }
}

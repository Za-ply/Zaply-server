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
import org.zapply.product.domain.user.dto.response.AccountsInfoResponse;
import org.zapply.product.domain.user.dto.response.LoginResponse;
import org.zapply.product.domain.user.dto.response.MemberResponse;
import org.zapply.product.domain.user.dto.response.TokenResponse;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.domain.user.repository.MemberRepository;
import org.zapply.product.domain.user.service.AccountService;
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
    private final AccountService accountService;
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

            // DB에서 회원 조회
            Long memberId = oAuth2User.getMemberId();
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        return new CoreException(GlobalErrorType.MEMBER_NOT_FOUND);
                    });

            // JWT 토큰 생성
            TokenResponse tokenResponse = jwtProvider.createToken(member);
            redisClient.setValue(member.getEmail(), tokenResponse.refreshToken(), refreshTokenExpirationTime);
            MemberResponse memberResponse = MemberResponse.of(member);
            AccountsInfoResponse accountsInfo = accountService.getAccountsInfo(member);
            LoginResponse loginResponse = LoginResponse.of(tokenResponse, memberResponse, accountsInfo);

            Cookie loginResponseCookie = new Cookie("loginResponse", URLEncoder.encode(new ObjectMapper().writeValueAsString(loginResponse), "UTF-8"));
            loginResponseCookie.setHttpOnly(true);
            loginResponseCookie.setSecure(true);
            loginResponseCookie.setPath("/");
            loginResponseCookie.setMaxAge((int)(accessTokenExpirationTime / 1000));
            response.addCookie(loginResponseCookie);

            Cookie accessToken = new Cookie("accessToken", tokenResponse.accessToken());
            accessToken.setHttpOnly(true);
            accessToken.setSecure(true);
            accessToken.setPath("/");
            accessToken.setMaxAge((int)(accessTokenExpirationTime / 1000));
            response.addCookie(accessToken);

            Cookie refreshToken = new Cookie("refreshToken", tokenResponse.refreshToken());
            refreshToken.setHttpOnly(true);
            refreshToken.setSecure(true);
            refreshToken.setPath("/");
            refreshToken.setMaxAge((int)(refreshTokenExpirationTime / 1000));
            response.addCookie(refreshToken);

            String targetUrl = "http://localhost:3000/google/callback";
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
        catch (IOException e) {
            throw new CoreException(GlobalErrorType.OAUTH_ERROR);
        }
    }
}

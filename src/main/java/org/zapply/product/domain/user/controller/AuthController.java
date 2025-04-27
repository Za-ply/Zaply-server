package org.zapply.product.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.user.dto.request.AuthRequest;
import org.zapply.product.domain.user.dto.request.SignInRequest;
import org.zapply.product.domain.user.dto.response.MemberResponse;
import org.zapply.product.domain.user.dto.response.TokenResponse;
import org.zapply.product.domain.user.service.AuthService;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.security.AuthDetails;
import org.zapply.product.global.security.jwt.JwtProvider;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${jwt.header}")
    private String tokenHeader;

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "사용자의 정보 입력 후 회원가입")
    public ApiResponse<MemberResponse> signUp(@Validated @RequestBody AuthRequest authRequest) {
        return ApiResponse.success(authService.signUp(authRequest));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "로그인 기능")
    public ApiResponse<TokenResponse> signIn(@Validated @RequestBody SignInRequest signInRequest) {
        return ApiResponse.success(authService.signIn(signInRequest));
    }

    @PostMapping("/sign-out")
    @Operation(summary = "로그아웃", description = "로그아웃 기능")
    public ApiResponse<?> signOut(HttpServletRequest request) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);
        String accessToken = jwtProvider.resolveAccessToken(request);

        if(refreshToken==null || accessToken==null) throw new CoreException(GlobalErrorType.TOKEN_NOT_FOUND);

        authService.signOut(refreshToken, accessToken);
        return ApiResponse.success("로그아웃 성공");
    }

    @GetMapping("/recreate")
    @Operation(summary = "토큰 재발급", description = "AccessToken 만료 시 RefreshToken으로 AccessToken 재발급")
    public ApiResponse<TokenResponse> recreate(HttpServletRequest request, @AuthenticationPrincipal AuthDetails authDetails) {
        String token = request.getHeader(tokenHeader);
        if(token ==null) throw new CoreException(GlobalErrorType.TOKEN_NOT_FOUND);
        return ApiResponse.success(authService.recreate(token, authDetails.getUser()));
    }
}
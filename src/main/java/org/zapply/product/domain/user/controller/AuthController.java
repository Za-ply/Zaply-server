package org.zapply.product.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.user.dto.request.AuthRequest;
import org.zapply.product.domain.user.dto.request.SignInRequest;
import org.zapply.product.domain.user.dto.response.MemberResponse;
import org.zapply.product.domain.user.dto.response.TokenResponse;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.domain.user.service.AuthService;
import org.zapply.product.domain.user.service.UserService;
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
    private final AccountService accountService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "사용자의 정보 입력 후 회원가입")
    public ApiResponse<MemberResponse> signUp(@Valid @RequestBody AuthRequest authRequest) {
        return ApiResponse.success(authService.signUp(authRequest));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "로그인 기능")
    public ApiResponse<TokenResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return ApiResponse.success(authService.signIn(signInRequest));
    }

    @PostMapping("/sign-out")
    @Operation(summary = "로그아웃", description = "로그아웃 기능")
    public ApiResponse<?> signOut(HttpServletRequest request) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);
        String accessToken = jwtProvider.resolveAccessToken(request);

        authService.signOut(refreshToken, accessToken);
        return ApiResponse.success("로그아웃 성공");
    }

    @GetMapping("/recreate")
    @Operation(summary = "토큰 재발급", description = "AccessToken 만료 시 RefreshToken으로 AccessToken 재발급")
    public ApiResponse<TokenResponse> recreate(HttpServletRequest request, @AuthenticationPrincipal AuthDetails authDetails) {
        String token = request.getHeader(tokenHeader);
        return ApiResponse.success(authService.recreate(token, authDetails.getMember()));
    }

    @GetMapping("/email/duplicate")
    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 확인. true - 중복, false - 사용 가능")
    public ApiResponse<Boolean> checkEmailDuplicate(@RequestParam("email") String email) {
        return ApiResponse.success(authService.checkEmailDuplicate(email));
    }

    @GetMapping()
    @Operation(summary = "사용자 정보 조회(이름, 이메일)", description = "jwt 토큰을 통해 사용자 정보 조회")
    public ApiResponse<MemberResponse> getUserInformation(@AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(MemberResponse.of(authDetails.getMember()));
    }

    // 사용자가 연동한 account를 조회
    @GetMapping("/account")
    @Operation(summary = "사용자 연동 계정 조회", description = "사용자가 연동한 계정 조회")
    public ApiResponse<?> getUserAccount(@AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(accountService.getAccountsInfo(authDetails.getMember()));
    }
}
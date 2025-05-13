package org.zapply.product.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.facebook.FacebookClient;
import org.zapply.product.global.security.AuthDetails;

import java.io.IOException;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final FacebookClient facebookClient;

    @GetMapping("/facebook/login")
    @Operation(summary = "페이스북 계정연동", description = "페이스북 계정연동")
    public void loginFacebook(HttpServletResponse response,
                              @AuthenticationPrincipal AuthDetails authDetails) throws IOException {
        response.sendRedirect(facebookClient.buildAuthorizationUri(authDetails.getMember()));
    }

    @GetMapping("/facebook/link")
    @Operation(summary = "페이스북 액세스 토큰 발급", description = "페이스북 액세스 토큰 발급")
    public ApiResponse<String> linkFacebook(@RequestParam("code") String code, @RequestParam("state") Long memberId) {
        return ApiResponse.success(accountService.linkFacebook(code, memberId));
    }

    @PostMapping("/threads/link")
    @Operation(summary = "스레드 계정 연동", description = "스레드 계정 연동")
    public ApiResponse<?> signInWithThreads(@RequestParam("code") String code, @AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(accountService.linkThreads(code, authDetails.getMember()));
    }
}

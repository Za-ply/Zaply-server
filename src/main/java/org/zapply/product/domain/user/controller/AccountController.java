package org.zapply.product.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.security.AuthDetails;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/facebook/link")
    @Operation(summary = "페이스북 계정 연동", description = "페이스북 계정 연동")
    public ApiResponse<?> signInWithGoogle(@RequestParam("code") String code, @AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(accountService.linkFacebook(code, authDetails.getMember()));
    }

    @PostMapping("/threads/link")
    @Operation(summary = "스레드 계정 연동", description = "스레드 계정 연동")
    public ApiResponse<?> signInWithThreads(@RequestParam("code") String code, @AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(accountService.linkThreads(code, authDetails.getMember()));
    }
}

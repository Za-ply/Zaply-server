package org.zapply.product.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.facebook.FacebookClient;
import org.zapply.product.global.security.AuthDetails;
import org.zapply.product.global.threads.ThreadsClient;

import java.io.IOException;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final FacebookClient facebookClient;
    private final ThreadsClient threadsClient;

    @GetMapping("/facebook/link")
    @Operation(summary = "페이스북 액세스 토큰 발급", description = "페이스북 액세스 토큰 발급 (계정연동 API에서 연결되는 URL)")
    public ApiResponse<String> linkFacebook(@RequestParam("code") String code, @AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(accountService.linkFacebook(code, authDetails.getMember().getId()));
    }

    @GetMapping("/threads/login")
    @Operation(summary = "스레드 계정연동", description = "스레드 계정연동 생성 (accessToken 필요)")
    public void loginThreads(HttpServletResponse response,
                              @AuthenticationPrincipal AuthDetails authDetails) throws IOException {
        System.out.println(threadsClient.buildAuthorizationUri(authDetails.getMember().getId()));
        response.sendRedirect(threadsClient.buildAuthorizationUri(authDetails.getMember().getId()));
    }

    @GetMapping("/threads/link")
    @Operation(summary = "스레드 액세스 토큰 발급", description = "스레드 액세스 토큰 발급 (계정연동 API에서 연결되는 URL)")
    public ApiResponse<String> signInWithThreads(@RequestParam("code") String code, @RequestParam(value="state") Long memberId){
        return ApiResponse.success(accountService.linkThreads(code, memberId));
    }

    @GetMapping("/{snsType}/unlink")
    @Operation(summary = "스레드 계정연동 해제", description = "스레드 계정연동 해제")
    public ApiResponse<?> unlinkThreads(@PathVariable("snsType") SNSType snsType, @AuthenticationPrincipal AuthDetails authDetails) {
        accountService.unlinkService(snsType, authDetails.getMember());
        return ApiResponse.success("계정 연동 해제 성공");
    }
}

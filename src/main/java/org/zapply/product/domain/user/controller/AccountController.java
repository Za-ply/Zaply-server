package org.zapply.product.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.facebook.FacebookClient;
import org.zapply.product.global.security.AuthDetails;
import org.zapply.product.global.threads.ThreadsClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpHeaders;

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
    public void linkFacebook(@RequestParam("code") String code, @RequestParam(value="state") Long memberId,
                             HttpServletResponse response) throws IOException{
        accountService.linkFacebook(code, memberId);
        String redirectUrl = "http://localhost:3000/facebook/callback";
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/threads/login")
    @Operation(summary = "스레드 계정연동", description = "스레드 계정연동 생성 (accessToken 필요)")
    public void loginThreads(HttpServletResponse response,
                              @AuthenticationPrincipal AuthDetails authDetails) throws IOException {
        System.out.println(threadsClient.buildAuthorizationUri(authDetails.getMember().getId()));
        response.sendRedirect(threadsClient.buildAuthorizationUri(authDetails.getMember().getId()));
    }

    @GetMapping("/{snsType}/unlink")
    @Operation(summary = "스레드 계정연동 해제", description = "스레드 계정연동 해제")
    public ApiResponse<?> unlinkThreads(@PathVariable("snsType") SNSType snsType, @AuthenticationPrincipal AuthDetails authDetails) {
        accountService.unlinkService(snsType, authDetails.getMember());
        return ApiResponse.success("계정 연동 해제 성공");
    }

    @GetMapping("/threads/link")
    @Operation(summary = "스레드 액세스 토큰 발급", description = "스레드 액세스 토큰 발급 (계정연동 API에서 연결되는 URL)")
    public void signInWithThreads(@RequestParam("code") String code, @RequestParam(value="state") Long memberId,
                                  HttpServletResponse response) throws IOException{
        accountService.linkThreads(code, memberId);
        String redirectUrl = "http://localhost:3000/threads/callback";
        response.sendRedirect(redirectUrl);
    }
}

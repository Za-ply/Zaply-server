package org.zapply.product.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.user.dto.response.MemberResponse;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.domain.user.service.MemberService;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.security.AuthDetails;

@Slf4j
@RestController
@RequestMapping("/v1/member")
@RequiredArgsConstructor
public class MemberController {
    private final AccountService accountService;
    private final MemberService memberService;

    @GetMapping()
    @Operation(summary = "사용자 정보 조회(이름, 이메일)", description = "jwt 토큰을 통해 사용자 정보 조회")
    public ApiResponse<MemberResponse> getUserInformation(@AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(MemberResponse.of(authDetails.getMember()));
    }

    // 사용자가 연동한 account를 조회
    @GetMapping("/accounts")
    @Operation(summary = "사용자 연동 계정 조회", description = "사용자가 연동한 계정 조회")
    public ApiResponse<?> getUserAccount(@AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(accountService.getAccountsInfo(authDetails.getMember()));
    }

    @PatchMapping()
    @Operation(summary = "사용자 이름 수정", description = "사용자 이름 수정")
    public ApiResponse<?> updateUserName(@AuthenticationPrincipal AuthDetails authDetails, @RequestParam("name") String name) {
        return ApiResponse.success(memberService.updateMemberName(authDetails.getMember(), name));
    }
}

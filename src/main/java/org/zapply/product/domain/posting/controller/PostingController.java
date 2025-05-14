package org.zapply.product.domain.posting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.ThreadsInsightResponse;
import org.zapply.product.domain.posting.dto.response.ThreadsPostingResponse;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.security.AuthDetails;
import org.zapply.product.global.threads.ThreadsInsightClient;
import org.zapply.product.global.threads.ThreadsPostingClient;

@RestController
@RequestMapping("/v1/posting")
@RequiredArgsConstructor
@Tag(name = "Posting", description = "게시글 발행 API")
public class PostingController {

    private final ThreadsPostingClient threadsPostingClient;
    private final ThreadsInsightClient threadsInsightClient;

    @PostMapping("/threads/{project_id}/single")
    @Operation(summary = "스레드 미디어 단일 발행하기", description = "단일 미디어를 업로드하는 메소드. (media 하나만 업로드)")
    public ApiResponse<ThreadsPostingResponse> createSingleMedia(@AuthenticationPrincipal AuthDetails authDetails,
                                                                 @Valid @RequestBody ThreadsPostingRequest request,
                                                                 @PathVariable("project_id") Long projectId) {
        return ApiResponse.success(
                threadsPostingClient.createSingleMedia(authDetails.getMember(), request, projectId)
        );
    }

    @PostMapping("/threads/{project_id}/carousel")
    @Operation(summary = "스레드 미디어 케러셀 발행하기", description = "캐러셀 미디어를 업로드하는 메소드. (media 여러개 업로드)")
    public ApiResponse<ThreadsPostingResponse> createCarouselMedia(@AuthenticationPrincipal AuthDetails authDetails,
                                                                   @Valid @RequestBody ThreadsPostingRequest request,
                                                                   @PathVariable("project_id") Long projectId) {
        return ApiResponse.success(
                threadsPostingClient.createCarouselMedia(authDetails.getMember(), request, projectId)
        );
    }

    @GetMapping("/threads/{posting_id}/insight")
    @Operation(summary = "스레드 게시물 인사이트 조회하기", description = "스레드 게시물의 인사이트를 조회하는 메소드.")
    public ApiResponse<ThreadsInsightResponse> getThreadsInsight(@AuthenticationPrincipal AuthDetails authDetails,
                                                                          @PathVariable("posting_id") Long postingId) {
        return ApiResponse.success(
                threadsInsightClient.getThreadsInsight(authDetails.getMember(), postingId));
    }
}
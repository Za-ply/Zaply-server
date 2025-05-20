package org.zapply.product.domain.posting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.ThreadsInsightResponse;
import org.zapply.product.domain.posting.service.PostingQueryService;
import org.zapply.product.domain.posting.service.PostingService;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.security.AuthDetails;
import org.zapply.product.global.threads.ThreadsInsightClient;
import org.zapply.product.global.threads.ThreadsPostingClient;

@Slf4j
@RestController
@RequestMapping("/v1/posting")
@RequiredArgsConstructor
@Tag(name = "Posting", description = "게시글 발행 API")
public class PostingController {

    private final ThreadsPostingClient threadsPostingClient;
    private final ThreadsInsightClient threadsInsightClient;
    private final PostingService postingService;
    private final PostingQueryService postingQueryService;

    @PostMapping("/threads/{project_id}/single")
    @Operation(summary = "스레드 미디어 단일 발행하기", description = "단일 미디어를 업로드하는 메소드. (media 하나만 업로드)")
    public ApiResponse<?> createSingleMedia(@AuthenticationPrincipal AuthDetails authDetails,
                                            @Valid @RequestBody ThreadsPostingRequest request,
                                            @PathVariable("project_id") Long projectId) {
        if (request.scheduledAt() != null) {
            postingService.createScheduledSingleMedia(authDetails.getMember(), request, projectId);
        }
        else{
            postingService.createSingleMedia(authDetails.getMember(), request, projectId);
        }
        return ApiResponse.success();
    }

    @PostMapping("/threads/{project_id}/carousel")
    @Operation(summary = "스레드 미디어 케러셀 발행하기", description = "캐러셀 미디어를 업로드하는 메소드. (media 여러개 업로드)")
    public ApiResponse<?> createCarouselMedia(@AuthenticationPrincipal AuthDetails authDetails,
                                              @Valid @RequestBody ThreadsPostingRequest request,
                                              @PathVariable("project_id") Long projectId) {
        if (request.scheduledAt() != null) {
            postingService.createScheduledCarouselMedia(authDetails.getMember(), request, projectId);
        }
        else{
            postingService.createCarouselMedia(authDetails.getMember(), request, projectId);
        }
        return ApiResponse.success();
    }


    @GetMapping("/threads/{posting_id}/insight")
    @Operation(summary = "스레드 게시물 인사이트 조회하기", description = "스레드 게시물의 인사이트를 조회하는 메소드.")
    public ApiResponse<ThreadsInsightResponse> getThreadsInsight(@AuthenticationPrincipal AuthDetails authDetails,
                                                                 @PathVariable("posting_id") Long postingId) {
        return ApiResponse.success(
                threadsInsightClient.getThreadsInsight(authDetails.getMember(), postingId));
    }

    @GetMapping("/threads/my-media")
    @Operation(summary = "SNS 게시물 리스트 조회하기", description = "SNS 게시물 리스트를 조회하는 메소드.")
    public ApiResponse<?> getThreadsMedia(@RequestParam("snsType") SNSType snsType,
                                                             @AuthenticationPrincipal AuthDetails authDetails) {
        switch (snsType) {
            case THREADS:
                return ApiResponse.success(postingQueryService.getAllThreadsMedia(authDetails.getMember()));
            default:
                return ApiResponse.success(null);
        }
    }

    @GetMapping("/threads/media")
    @Operation(summary = "SNS 단일 게시물 조회하기", description = "SNS 단일 게시물을 조회하는 메소드.")
    public ApiResponse<?> getSingleThreadsMedia(@RequestParam("snsType") SNSType snsType,
                                                @RequestParam("mediaId") String mediaId,
                                                @AuthenticationPrincipal AuthDetails authDetails) {
        switch (snsType) {
            case THREADS:
                return ApiResponse.success(postingQueryService.getSingleThreadsMedia(authDetails.getMember(), mediaId));
            default:
                return ApiResponse.success(null);
        }
    }
}
package org.zapply.product.domain.posting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.PostingInfoResponse;
import org.zapply.product.domain.posting.service.PostingQueryService;
import org.zapply.product.domain.posting.service.PublishPostingService;
import org.zapply.product.domain.posting.dto.response.ThreadsInsightResponse;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.security.AuthDetails;
import org.zapply.product.global.threads.ThreadsInsightClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/posting")
@RequiredArgsConstructor
@Tag(name = "Posting", description = "게시글 발행 API")
public class PostingController {

    private final PublishPostingService publishPostingService;
    private final PostingQueryService postingQueryService;
    private final ThreadsInsightClient threadsInsightClient;

    // 사용자의 프로젝트에 존재하는 포스팅 조회를 위한 API
    @GetMapping("/{project_id}")
    @Operation(summary = "프로젝트(컨텐츠)별 포스팅 조회", description = "프로젝트(컨텐츠)별 발행된 포스팅 내용 및 예약시간 조회")
    public ApiResponse<List<PostingInfoResponse>> getProjectList(@AuthenticationPrincipal AuthDetails authDetails,
                                                                 @PathVariable("project_id") Long projectId){
        return ApiResponse.success(postingQueryService.getPostings(authDetails.getMember(), projectId));
    }


    @PostMapping("/threads/{project_id}/single")
    @Operation(summary = "스레드 미디어 단일 발행하기", description = "단일 미디어를 업로드하는 메소드. (media 하나만 업로드)")
    public ApiResponse<?> createSingleMedia(@AuthenticationPrincipal AuthDetails authDetails,
                                            @Valid @RequestBody ThreadsPostingRequest request,
                                            @PathVariable("project_id") Long projectId) {
        if (request.scheduledAt() != null) {
            publishPostingService.createScheduledSingleMedia(authDetails.getMember(), request, projectId);
        }
        else{
            publishPostingService.createSingleMedia(authDetails.getMember(), request, projectId);
        }
        return ApiResponse.success();
    }

    @PostMapping("/threads/{project_id}/carousel")
    @Operation(summary = "스레드 미디어 케러셀 발행하기", description = "캐러셀 미디어를 업로드하는 메소드. (media 여러개 업로드)")
    public ApiResponse<?> createCarouselMedia(@AuthenticationPrincipal AuthDetails authDetails,
                                              @Valid @RequestBody ThreadsPostingRequest request,
                                              @PathVariable("project_id") Long projectId) {
        if (request.scheduledAt() != null) {
            publishPostingService.createScheduledCarouselMedia(authDetails.getMember(), request, projectId);
        }
        else{
            publishPostingService.createCarouselMedia(authDetails.getMember(), request, projectId);
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

    @PutMapping("/{postingId}/schedule")
    public ApiResponse<?> updateSchedule(
            @PathVariable Long postingId,
            @Valid @RequestBody UpdateScheduleRequest request
    ) {
        publishPostingService.updateScheduledTime(postingId, request.scheduledAt());
        return ApiResponse.success();
    }

    public record UpdateScheduleRequest(
            @NotNull
            @Schema(description = "새로운 예약 시간 (ISO-8601)", example = "2025-05-20T15:30:00")
            LocalDateTime scheduledAt
    ) {}

}
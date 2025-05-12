package org.zapply.product.domain.posting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.ThreadsPostingResponse;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.security.AuthDetails;
import org.zapply.product.global.threads.ThreadsPostingClient;

@RestController
@RequestMapping("/v1/posting")
@RequiredArgsConstructor
@Tag(name = "Posting", description = "게시글 발행 API")
public class PostingController {

    private final ThreadsPostingClient threadsPostingClient;

    @PostMapping("/threads/{project_id}")
    @Operation(summary = "스레드 미디어 발행하기", description = "스레드 미디어 발행하기(단일컨테이너)")
    public ApiResponse<ThreadsPostingResponse> createMedia(@AuthenticationPrincipal AuthDetails authDetails,
                                                                   @Valid @RequestBody ThreadsPostingRequest request,
                                                                   @PathVariable("project_id") Long projectId) {
        return ApiResponse.success(
                threadsPostingClient.createMedia(authDetails.getMember(), request, projectId)
        );
    }
}

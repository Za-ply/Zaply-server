package org.zapply.product.domain.posting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.posting.dto.request.ToneTransferRequest;
import org.zapply.product.domain.posting.service.ToneTransferService;
import org.zapply.product.global.apiPayload.response.ApiResponse;

@RestController
@RequestMapping("/v1/posting")
@RequiredArgsConstructor
@Tag(name = "Posting", description = "게시글 발행 API")
public class ToneTransferController {

    private final ToneTransferService toneTransferService;

    @PostMapping("/transfer")
    @Operation(summary = "SNS 글 분위기 변환", description = "입력된 콘텐츠를 지정된 SNS 타입에 맞춰 변환합니다.")
    public ApiResponse<String> transferToSNSTone(@Valid @RequestBody ToneTransferRequest toneTransferRequest) {
        return ApiResponse.success(toneTransferService.TransferToSNSTone(toneTransferRequest));
    }

}

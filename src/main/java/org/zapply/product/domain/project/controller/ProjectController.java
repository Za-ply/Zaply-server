package org.zapply.product.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zapply.product.domain.project.service.ProjectService;
import org.zapply.product.global.apiPayload.response.ApiResponse;
import org.zapply.product.global.security.AuthDetails;

@RestController
@RequestMapping("/v1/project")
@RequiredArgsConstructor
@Tag(name = "Project", description = "프로젝트 API")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    @Operation(summary = "빈 프로젝트 생성하기", description = "(SNS 발행) 프로젝트 생성 시에 빈 프로젝트 생성하기")
    public ApiResponse<?> createEmptyProject(@AuthenticationPrincipal AuthDetails authDetails) {
        return ApiResponse.success(projectService.createEmptyProject(authDetails.getMember()));
    }
}

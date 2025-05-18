package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.posting.dto.response.PostingInfoResponse;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.posting.repository.PostingRepository;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.project.service.ImageService;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostingQueryService {

    private final PostingRepository postingRepository;
    private final ProjectRepository projectRepository;
    private final ImageService imageService;

    public List<PostingInfoResponse> getPostings(Member member, Long projectId) {

        // 타인 프로젝트 조회 방지
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.PROJECT_NOT_FOUND));

        if (!project.getMember().getId().equals(member.getId())) {
            throw new CoreException(GlobalErrorType.IS_NOT_USER_PROJECT);
        }

        List<Posting> postings = postingRepository.findAllByProject_ProjectIdAndDeletedAtIsNull(projectId);
        if (postings.isEmpty()) {throw new CoreException(GlobalErrorType.POSTING_NOT_FOUND);}

        return postings.stream()
                .map(posting -> PostingInfoResponse.of(
                        posting,
                        imageService.getImagesURLByPosting(posting)
                ))
                .toList();
    }
}

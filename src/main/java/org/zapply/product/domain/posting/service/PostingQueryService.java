package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.posting.dto.response.PostingInfoResponse;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.posting.repository.PostingRepository;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.domain.user.service.AccountService;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.threads.ThreadsMediaClient;
import org.zapply.product.global.threads.ThreadsMediaResponse;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostingQueryService {

    private final ThreadsMediaClient threadsMediaClient;
    private final AccountService accountService;
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
    /**
     * 스레드 미디어 조회하기
     *
     * @param member
     * @param
     * @return 스레드 미디어 조회 결과
     */
    public List<ThreadsMediaResponse.ThreadsMedia> getAllThreadsMedia( Member member) {
        // accessToken 가져오기
        String accessToken = accountService.getAccessToken(member, SNSType.THREADS);
        // 스레드 API에 요청하여 미디어 데이터 가져오기
        return threadsMediaClient.getAllThreadsMedia(accessToken);
    }

    /**
     * 스레드 단일 게시물 조회하기
     * @param member
     * @param mediaId
     */
    public ThreadsMediaResponse.ThreadsMedia getSingleThreadsMedia(Member member, String mediaId) {
        // accessToken 가져오기
        String accessToken = accountService.getAccessToken(member, SNSType.THREADS);
        // 스레드 API에 요청하여 미디어 데이터 가져오기
        return threadsMediaClient.getSingleThreadsMedia(accessToken, mediaId);
    }
}

package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.posting.dto.response.CursorSlice;
import org.zapply.product.domain.posting.dto.response.PostingDetailResponse;
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
import org.zapply.product.global.snsClients.instagram.InstagramMediaClient;
import org.zapply.product.global.snsClients.instagram.InstagramMediaResponse;
import org.zapply.product.global.snsClients.threads.ThreadsMediaClient;
import org.zapply.product.global.snsClients.threads.ThreadsMediaResponse;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostingQueryService {

    private final ThreadsMediaClient threadsMediaClient;
    private final AccountService accountService;
    private final PostingRepository postingRepository;
    private final ProjectRepository projectRepository;
    private final ImageService imageService;
    private final InstagramMediaClient instagramMediaClient;

    public List<PostingInfoResponse> getPostings(Member member, Long projectId) {

        // 타인 프로젝트 조회 방지
        boolean isUserProject = projectRepository.existsByProjectIdAndMember_Id(projectId, member.getId());
        if (!isUserProject) {
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

    public CursorSlice<PostingDetailResponse> getAllInstagramMedia(Member member, String cursor, int size) {
        List<InstagramMediaResponse.Data> response = instagramMediaClient.getAllMedia(member);

        List<InstagramMediaResponse.Data> filtered = response.stream()
                .filter(media -> "IMAGE".equals(media.media_type()) ||
                        "CAROUSEL_ALBUM".equals(media.media_type()) ||
                        "TEXT_POST".equals(media.media_type()))
                .toList();

        int startIndex = 0;
        if (cursor != null) {
            for (int i = 0; i < filtered.size(); i++) {
                if (filtered.get(i).id().equals(cursor)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        int endIndex = Math.min(startIndex + size, filtered.size());
        List<InstagramMediaResponse.Data> pagedMedia = filtered.subList(startIndex, endIndex);

        List<PostingDetailResponse> content = pagedMedia.stream()
                .map(media -> PostingDetailResponse.of(
                        media.id(),
                        "",
                        media.caption(),
                        media.timestamp(),
                        switch (media.media_type()) {
                            case "IMAGE", "CAROUSEL_ALBUM" -> media.media_urls();
                            case "TEXT_POST" -> null;
                            default -> null;
                        }
                ))
                .collect(Collectors.toList());

        String nextCursor = endIndex < filtered.size() ? filtered.get(endIndex - 1).id() : null;
        boolean hasNext = nextCursor != null;

        return new CursorSlice<>(content, nextCursor, hasNext);
    }
}

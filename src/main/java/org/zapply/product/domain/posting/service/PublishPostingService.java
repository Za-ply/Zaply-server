package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.ThreadsPostingResponse;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.posting.enumerate.MediaType;
import org.zapply.product.domain.posting.enumerate.PostingState;
import org.zapply.product.domain.posting.repository.PostingRepository;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.scheduler.service.SchedulingService;
import org.zapply.product.global.threads.ThreadsPostingClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublishPostingService {

    private final ThreadsPostingClient threadsPostingClient;
    private final ProjectRepository projectRepository;
    private final ImageService imageService;
    private final PostingRepository postingRepository;
    private final SchedulingService schedulingService;

    // 스레드 미디어 단일 발행하기
    public void publishSingleMediaNow(Member member, ThreadsPostingRequest request, Long projectId) {
        threadsPostingClient.createSingleMedia(member, request, projectId);
    }

    // 스레드 미디어 단일 예약 발행하기
    @Transactional
    public void scheduleSingleMediaPublish(ThreadsPostingRequest request, Long projectId) {
        // 프로젝트 검증
        Project project = projectRepository
                .findByProjectIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.PROJECT_NOT_FOUND));

        // Posting 엔티티 생성 및 저장
        Posting posting = Posting.builder()
                .project(project)
                .postingType(SNSType.THREADS)
                .postingContent(request.text())
                .scheduledAt(request.scheduledAt())
                .postingState(PostingState.SCHEDULED)
                .build();

        postingRepository.save(posting);
        imageService.saveAllImages(posting, request.media());

        schedulingService.scheduleTask(
                posting.getPostingId(),
                request.scheduledAt(),
                () -> executeScheduledSingleMedia(posting.getPostingId())
        );
    }

    @Transactional
    public void rescheduleSingleMedia(Long postingId, LocalDateTime newScheduledAt) {
        Posting posting = postingRepository.findByPostingIdAndPostingStateAndDeletedAtIsNull(postingId, PostingState.SCHEDULED)
                .orElseThrow(() -> new CoreException(GlobalErrorType.POSTING_NOT_FOUND));

        // 기존 일정 삭제 및 재등록
        schedulingService.cancelTask(postingId);
        posting.updateScheduledAt(newScheduledAt);

        schedulingService.scheduleTask(
                postingId,
                newScheduledAt,
                () -> executeScheduledSingleMedia(postingId)
        );
    }

    @Transactional
    public void executeScheduledSingleMedia(Long postingId) {
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.POSTING_NOT_FOUND));

        List<String> imageUrls = imageService.getImagesURLByPosting(posting);
        Member member = posting.getProject().getMember();
        ThreadsPostingRequest threadsPostingRequest = ThreadsPostingRequest.of(MediaType.IMAGE, imageUrls, posting);

        String mediaId = threadsPostingClient.createUpdatedSingleMedia(member, threadsPostingRequest);
        posting.updatePostingState(PostingState.POSTED);
        posting.updateMediaId(mediaId);
    }

    // 스레드 미디어 캐러셀(다중) 발행하기
    public ThreadsPostingResponse publishCarouselMediaNow(Member member, ThreadsPostingRequest request, Long projectId) {
        return threadsPostingClient.createCarouselMedia(member, request, projectId);
    }

    @Transactional
    public void scheduleCarouselMediaPublish(ThreadsPostingRequest request, Long projectId) {
        Project project = projectRepository.findByProjectIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.PROJECT_NOT_FOUND));

        Posting posting = Posting.builder()
                .project(project)
                .postingType(SNSType.THREADS)
                .postingContent(request.text())
                .scheduledAt(request.scheduledAt())
                .postingState(PostingState.SCHEDULED)
                .build();
        postingRepository.save(posting);
        imageService.saveAllImages(posting, request.media());

        schedulingService.scheduleTask(
                posting.getPostingId(),
                request.scheduledAt(),
                () -> executeScheduledCarouselMedia(posting.getPostingId())
        );
    }

    @Transactional
    public void rescheduleCarouselMedia(Long postingId, LocalDateTime newScheduledAt) {
        Posting posting = postingRepository
                .findByPostingIdAndPostingStateAndDeletedAtIsNull(postingId, PostingState.SCHEDULED)
                .orElseThrow(() -> new CoreException(GlobalErrorType.POSTING_NOT_FOUND));

        schedulingService.cancelTask(postingId);
        posting.updateScheduledAt(newScheduledAt);

        schedulingService.scheduleTask(
                postingId,
                newScheduledAt,
                () -> executeScheduledCarouselMedia(postingId)
        );
    }

    @Transactional
    public void executeScheduledCarouselMedia(Long postingId) {
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.POSTING_NOT_FOUND));

        List<String> mediaUrls = imageService.getImagesURLByPosting(posting);
        Member member = posting.getProject().getMember();
        ThreadsPostingRequest threadsPostingRequest = ThreadsPostingRequest.of(MediaType.IMAGE, mediaUrls, posting);

        String mediaId = threadsPostingClient.createUpdatedCarouselMedia(member, threadsPostingRequest);
        posting.updatePostingState(PostingState.POSTED);
        posting.updateMediaId(mediaId);
    }
}

package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.ThreadsPostingResponse;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.posting.enumerate.PostingState;
import org.zapply.product.domain.posting.repository.PostingRepository;
import org.zapply.product.domain.project.entity.Project;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.scheduler.service.JobScheduler;
import org.zapply.product.global.scheduler.task.SchedulingService;
import org.zapply.product.global.threads.ThreadsPostingClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublishPostingService {

    private final JobScheduler jobScheduler;
    private final ThreadsPostingClient threadsPostingClient;
    private final ProjectRepository projectRepository;
    private final ImageService imageService;
    private final PostingRepository postingRepository;
    private final SchedulingService schedulingService;

    // 스레드 미디어 단일 예약 발행하기
    @Transactional
    public void createScheduledSingleMedia(Member member,
                                           ThreadsPostingRequest request,
                                           Long projectId) {
        // 프로젝트 검증
        Project project = projectRepository
                .findByProjectIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.PROJECT_NOT_FOUND));

        log.info("projectId : {}, scheduledAt : {}", projectId, request.scheduledAt());

        // Posting 엔티티 생성 및 저장
        Posting posting = Posting.builder()
                .project(project)
                .postingType(SNSType.THREADS)
                .postingContent(request.text())
                .scheduledAt(request.scheduledAt())
                .postingState(PostingState.SCHEDULED)
                .build();

        postingRepository.save(posting);

        // 이미지 저장
        imageService.saveAllImages(posting, request.media());

        // 스케줄 등록: SchedulingService 사용
        schedulingService.scheduleTask(
                posting.getPostingId(),
                request.scheduledAt(),
                () -> threadsPostingClient.createSingleMedia(member, request, projectId)
        );
        System.out.println("스케쥴 예약됨 postingId : " + posting.getPostingId());
    }


    // 스레드 미디어 단일 발행하기
    public void createSingleMedia(Member member, ThreadsPostingRequest request, Long projectId) {
        threadsPostingClient.createSingleMedia(member, request, projectId);
    }

    // 스레드 미디어 캐러셀(다중) 예약 발행하기
    public void createScheduledCarouselMedia(Member member, ThreadsPostingRequest request, Long projectId) {
        log.info("projectId : {}, scheduledAt : {}", projectId, request.scheduledAt());

        // 프로젝트가 존재하는지 확인
        Project project = projectRepository.findByProjectIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.PROJECT_NOT_FOUND));

        Posting posting = Posting.builder()
                .project(project)
                .postingType(SNSType.THREADS)
                .postingContent(request.text())
                .scheduledAt(request.scheduledAt())
                .postingState(PostingState.SCHEDULED)
                .build();

        imageService.saveAllImages(posting, request.media());

        schedulingService.scheduleTask(
                posting.getPostingId(),
                request.scheduledAt(),
                () -> threadsPostingClient.createCarouselMedia(member, request, projectId)
        );
    }

    // 스레드 미디어 캐러셀(다중) 발행하기
    public ThreadsPostingResponse createCarouselMedia(Member member, ThreadsPostingRequest request, Long projectId) {
        return threadsPostingClient.createCarouselMedia(member, request, projectId);
    }

    @Transactional
    public void updateScheduledTime(Long postingId, LocalDateTime newScheduledAt) {
        // 1) Posting 조회
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.POSTING_NOT_FOUND));
        log.info("postingId : {}, ScheduledAt : {}", posting.getPostingId(), posting.getScheduledAt());

        // 2) 기존 스케줄 취소
        schedulingService.cancelTask(postingId);
        System.out.println("스케줄 취소됨 postingId : " + postingId);

        // 3) Posting 엔티티 예약 시간 업데이트 저장
        posting.updateScheduledAt(newScheduledAt);
        postingRepository.save(posting);
        log.info("postingId : {}, newScheduledAt : {}", posting.getPostingId(), posting.getScheduledAt());

        // 4) 기존 이미지 URL 가져와 새로운 요청 생성
        List<String> mediaUrls = imageService.getImagesURLByPosting(posting);

        // 5) 새로운 스케줄 등록
        schedulingService.scheduleTask(
                postingId,
                newScheduledAt,
                () -> executeScheduledSingleMedia(postingId)
        );
    }

    @Transactional
    public void executeScheduledSingleMedia(Long postingId) {
        // ① 트랜잭션 안에서 Posting 조회
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.POSTING_NOT_FOUND));

        // ② 필요한 연관 데이터(이미지 URL, project, member 등) 모두 이 시점에 로드
        List<String> mediaUrls = imageService.getImagesURLByPosting(posting);
        Member member = posting.getProject().getMember();
        Long projectId = posting.getProject().getProjectId();
        LocalDateTime scheduledAt = posting.getScheduledAt();
        String text = posting.getPostingContent();

        // ③ 요청 객체 재생성
        ThreadsPostingRequest freshReq =
                new ThreadsPostingRequest("IMAGE", mediaUrls, text, scheduledAt);

        // ④ 실제 업로드/발행 호출
        threadsPostingClient.createSingleMedia(member, freshReq, projectId);
    }
}

package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.ThreadsPostingResponse;
import org.zapply.product.domain.posting.entity.Posting;
import org.zapply.product.domain.posting.enumerate.PostingState;
import org.zapply.product.domain.project.repository.ProjectRepository;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.clova.enuermerate.SNSType;
import org.zapply.product.global.scheduler.service.JobScheduler;
import org.zapply.product.global.threads.ThreadsPostingClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishPostingService {

    private final JobScheduler jobScheduler;
    private final ThreadsPostingClient threadsPostingClient;
    private final ProjectRepository projectRepository;

    // 스레드 미디어 단일 예약 발행하기
    public void createScheduledSingleMedia(Member member, ThreadsPostingRequest request, Long projectId) {

        log.info("projectId : {}, scheduledAt : {}", projectId, request.scheduledAt());
        Posting posting = Posting.builder()
                .project(projectRepository.getById(projectId))
                .postingType(SNSType.THREADS)
                .postingState(PostingState.RESERVED)
                .postingLink("이건 어케아냐") // 여기에 게시글 링크
                .mediaId("") // 여기에 그림 이미지
                .build();
        // s3에 업로드하고,  이미지 엔티티에 필드 생성하고, 프스팅 생성하고(프로젝트, 포스팅 제목, 이함수에선쓰레드, 상태, 생성된 게시글 링크 없음, 이미지 아이디들? )
        // 포스팅 생성
        // 포스팅 상태 변경
        Long jobId = posting.getPostingId();
        jobScheduler.schedule(
                jobId,
                request.scheduledAt(),
                () -> threadsPostingClient.createSingleMedia(member, request, projectId)
        );
    }

    // 스레드 미디어 단일 발행하기
    public void createSingleMedia(Member member, ThreadsPostingRequest request, Long projectId) {
        threadsPostingClient.createSingleMedia(member, request, projectId);
    }

    // 스레드 미디어 캐러셀(다중) 예약 발행하기
    public void createScheduledCarouselMedia(Member member, ThreadsPostingRequest request, Long projectId) {
        log.info("projectId : {}, scheduledAt : {}", projectId, request.scheduledAt());
        Long jobId = jobScheduler.generateJobId();
        jobScheduler.schedule(
                jobId,
                request.scheduledAt(),
                () -> threadsPostingClient.createCarouselMedia( member, request, projectId)
        );
    }

    // 스레드 미디어 캐러셀(다중) 발행하기
    public ThreadsPostingResponse createCarouselMedia(Member member, ThreadsPostingRequest request, Long projectId) {
        return threadsPostingClient.createCarouselMedia(member, request, projectId);
    }
}

package org.zapply.product.domain.posting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zapply.product.domain.posting.dto.request.ThreadsPostingRequest;
import org.zapply.product.domain.posting.dto.response.ThreadsPostingResponse;
import org.zapply.product.domain.user.entity.Member;
import org.zapply.product.global.scheduler.service.JobScheduler;
import org.zapply.product.global.threads.ThreadsPostingClient;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostingService {

    private final JobScheduler jobScheduler;
    private final ThreadsPostingClient threadsPostingClient;

    // 스레드 미디어 단일 예약 발행하기
    public void createScheduledSingleMedia(Member member, ThreadsPostingRequest request, Long projectId) {
        log.info("projectId : {}, scheduledAt : {}", projectId, request.scheduledAt());
        Long jobId = jobScheduler.generateJobId();
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

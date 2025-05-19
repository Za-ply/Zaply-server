package org.zapply.product.global.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;
import org.zapply.product.global.scheduler.task.SchedulingService;

/**
 * 애플리케이션 구동 후, DB에 남아 있는 SCHEDULED 상태의 작업을
 * SchedulingService를 통해 다시 스케줄링합니다.
 */
@Component
@RequiredArgsConstructor
public class SchedulerInitializer {

    private final ScheduledJobRepository jobRepository;
    private final SchedulingService schedulingService;
    private final ScheduledJobService scheduledJobService;

    @EventListener(ApplicationReadyEvent.class)
    public void restorePendingJobs() {
        jobRepository.findAllByStatus(JobStatus.SCHEDULED)
                .forEach(job ->
                        schedulingService.scheduleTask(
                                job.getPostingId(),
                                job.getExecuteAt(),
                                // 실제 실행 시, DB 상태만 COMPLETED로 마킹
                                () -> scheduledJobService.completeJob(job.getId())
                        )
                );
    }
}


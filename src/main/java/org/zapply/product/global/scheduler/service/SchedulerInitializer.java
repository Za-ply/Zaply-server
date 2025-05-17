package org.zapply.product.global.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;


@Component
@RequiredArgsConstructor
public class SchedulerInitializer {

    private final ScheduledJobRepository jobRepository;
    private final JobScheduler jobScheduler;

    @EventListener(ApplicationReadyEvent.class)
    public void restorePendingJobs() {
        jobRepository.findAllByStatus(JobStatus.SCHEDULED)
                .forEach(job ->
                        jobScheduler.schedule(
                                job.getPostingId(),
                                job.getExecuteAt(),
                                () -> jobScheduler.completeJob(job.getId())
                        )
                );
    }
}



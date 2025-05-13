package org.zapply.product.global.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.scheduler.entity.ScheduledJob;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class JobSchedulerImpl implements JobScheduler {

    private final ScheduledJobRepository jobRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final ScheduledJobService scheduledJobService;
    private final ZoneId zone = ZoneId.systemDefault();

    @Override
    public ScheduledFuture<?> schedule(Long postingId, LocalDateTime executeAt, Runnable task) {
        ScheduledJob job = ScheduledJob.builder()
                .postingId(postingId)
                .executeAt(executeAt)
                .status(JobStatus.SCHEDULED)
                .build();
        jobRepository.save(job);

        Date runAt = Date.from(executeAt.atZone(zone).toInstant());

        return taskScheduler.schedule(() -> {
            try {
                task.run();
                scheduledJobService.completeJob(job.getId());
            } catch (Exception e) {
                job.updateStatus(JobStatus.FAILED);
                jobRepository.save(job);
                throw new CoreException(GlobalErrorType.SCHEDULED_JOB_EXECUTION_ERROR);
            }
        }, runAt);
    }

    @Override
    public void completeJob(Long jobId) {
        scheduledJobService.completeJob(jobId);
    }
}


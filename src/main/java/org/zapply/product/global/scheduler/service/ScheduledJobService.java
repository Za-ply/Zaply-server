package org.zapply.product.global.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.scheduler.entity.ScheduledJob;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;

@Service
@RequiredArgsConstructor
public class ScheduledJobService {

    private final ScheduledJobRepository scheduledJobRepository;

    @Transactional
    public void completeJob(Long jobId) {
        ScheduledJob job = scheduledJobRepository.findById(jobId)
                .orElseThrow(() -> new CoreException(GlobalErrorType.SCHEDULED_JOB_NOT_FOUND));

        job.updateStatus(JobStatus.COMPLETED);
    }
}
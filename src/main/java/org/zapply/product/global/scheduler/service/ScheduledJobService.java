package org.zapply.product.global.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;

@Service
@RequiredArgsConstructor
public class ScheduledJobService {

    private final ScheduledJobRepository jobRepository;

    @Transactional
    public void completeJob(Long jobId) {
        jobRepository.findById(jobId).ifPresent(job -> {
            job.updateStatus(JobStatus.COMPLETED);
        });
    }
}
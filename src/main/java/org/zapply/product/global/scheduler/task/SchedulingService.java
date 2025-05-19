package org.zapply.product.global.scheduler.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zapply.product.global.scheduler.entity.ScheduledJob;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class SchedulingService {
    private final ScheduledJobRepository jobRepo;
    private final SchedulingRepository schedulingRepo;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final TransactionTemplate txTemplate;
    private final ZoneId zone = ZoneId.systemDefault();

    public Long scheduleTask(Long postingId, LocalDateTime execAt, Runnable action) {
        // 1) DB에 ScheduledJob 저장
        ScheduledJob job = ScheduledJob.builder()
                .postingId(postingId)
                .executeAt(execAt)
                .status(JobStatus.SCHEDULED)
                .build();
        jobRepo.save(job);

        System.out.println("SchedulingService.scheduleTask, jobId: " + job.getId());

        // 2) 실제 스케줄 등록
        Date runAt = Date.from(execAt.atZone(zone).toInstant());
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            // 작업 실행 및 상태 업데이트를 트랜잭션으로 보장
            txTemplate.execute(status -> {
                try {
                    action.run();
                    System.out.println("SchedulingService.scheduleTask, 작업 실행됨 + COMPLETED");
                    job.updateStatus(JobStatus.COMPLETED);
                } catch (Exception e) {
                    System.out.println("SchedulingService.scheduleTask, 작업 실행됨 + FAILED");
                    System.out.println(e.getMessage());
                    job.updateStatus(JobStatus.FAILED);
                }
                jobRepo.save(job);
                return null;
            });
            // 실행 후 메모리에서 future 제거
            schedulingRepo.remove(job.getId());
        }, runAt);

        // 3) 메모리에 future 보관
        schedulingRepo.add(job.getId(), future);
        return job.getId();
    }

    public void cancelTask(Long postingId) {
        // 1) DB에서 해당 postingId로 Pending 잡 조회
        ScheduledJob job = jobRepo.findByPostingIdAndStatus(postingId, JobStatus.SCHEDULED)
                .orElseThrow(() -> new IllegalArgumentException("No scheduled job for posting: " + postingId));
        Long jobId = job.getId();

        // 2) 메모리에서 future 취소
        ScheduledFuture<?> future = schedulingRepo.remove(jobId);
        if (future != null) {
            System.out.println("future found: " + future.toString());
            System.out.println("future canceled?: " + future.isCancelled());
            System.out.println("future done?: " + future.isDone());
            System.out.println("future state" + future.state());
            future.cancel(false);
        }
        // 3) DB에서 job 상태 업데이트
        job.updateStatus(JobStatus.CANCELED);
        jobRepo.save(job);
    }
}


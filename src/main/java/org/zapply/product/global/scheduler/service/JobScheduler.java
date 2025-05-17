package org.zapply.product.global.scheduler.service;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

public interface JobScheduler {
    /**
     * @param executeAt 실행 시점
     * @param task      실행할 Runnable 작업
     * @return 예약된 작업을 취소·상태 확인할 수 있는 ScheduledFuture
     */
    ScheduledFuture<?> schedule(Long postingId, LocalDateTime executeAt, Runnable task);

    void completeJob(Long jobId);

    Long generateJobId();
}

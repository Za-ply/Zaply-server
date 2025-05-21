package org.zapply.product.global.scheduler.task;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import org.zapply.product.global.scheduler.entity.ScheduledJob;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;
import org.zapply.product.global.scheduler.repository.SchedulingRepository;
import org.zapply.product.global.scheduler.service.SchedulingService;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SchedulingServiceTest {

    @Autowired
    private SchedulingService schedulingService;

    @Autowired
    private ScheduledJobRepository jobRepo;

    @Autowired
    private SchedulingRepository schedulingRepo;

    @Autowired
    private TransactionTemplate txTemplate;

    @AfterEach
    void tearDown() {
        // Clear DB and in-memory futures
        jobRepo.deleteAll();
    }

    @Test
    void scheduleTask_executesActionAndUpdatesStatus() throws InterruptedException {
        // given
        Long postingId = 1L;
        AtomicBoolean ran = new AtomicBoolean(false);
        System.out.println(LocalDateTime.now());
        LocalDateTime execAt = LocalDateTime.now().plusSeconds(3);
        System.out.println(execAt);
        Runnable action = () -> ran.set(true);

        // when
        Long jobId = schedulingService.scheduleTask(postingId, execAt, action);

        // then
        // wait a bit beyond execution time
        Thread.sleep(3000);

        // action should have run
        assertThat(ran.get()).isTrue();

        // DB entry status should be COMPLETED
        ScheduledJob job = jobRepo.findById(jobId).orElseThrow();
        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);

        // in-memory future should be removed or done
        assertThat(schedulingRepo.exists(jobId)).isFalse();
    }

    @Test
    void cancelTask_preventsExecutionAndUpdatesStatus() throws InterruptedException {
        // given
        Long postingId = 2L;
        AtomicBoolean ran = new AtomicBoolean(false);
        LocalDateTime execAt = LocalDateTime.now().plusSeconds(2);
        Runnable action = () -> ran.set(true);

        // schedule
        Long jobId = schedulingService.scheduleTask(postingId, execAt, action);
        // immediately cancel
        schedulingService.cancelTask(jobId);

        // wait past original execution time
        Thread.sleep(2500);

        // action should not have run
        assertThat(ran.get()).isFalse();

        // DB entry status should be CANCELED
        ScheduledJob job = jobRepo.findById(jobId).orElseThrow();
        assertThat(job.getStatus()).isEqualTo(JobStatus.CANCELED);

        // in-memory future should no longer exist
        assertThat(schedulingRepo.exists(jobId)).isFalse();
    }
}
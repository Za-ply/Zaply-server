package org.zapply.product.global.scheduler;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.zapply.product.global.scheduler.entity.ScheduledJob;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;
import org.zapply.product.global.scheduler.service.JobScheduler;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SchedulerTest {

    @Autowired
    private JobScheduler jobScheduler;
    @Autowired
    private ScheduledJobRepository jobRepository;

    private final Logger log = LoggerFactory.getLogger(SchedulerTest.class);

    // 테스트 컨텍스트 전용 빈 정의
    @TestConfiguration
    static class SchedulerTestConfig {
        @Bean
        public ThreadPoolTaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(1);
            scheduler.setThreadNamePrefix("test-sched-");
            scheduler.initialize();
            return scheduler;
        }
    }

    @Test
    public void testScheduleSuccess() throws InterruptedException {
        LocalDateTime runAt = LocalDateTime.now().plusSeconds(2);
        ScheduledFuture<?> future = jobScheduler.schedule(
                100L, runAt,
                () -> log.info("✅ 정상 실행: {}", LocalDateTime.now())
        );

        log.info("🕒 정상 작업 예약: 실행 시각 = {}", runAt);
        // 실행 시각 + 여유 1초 후 완료 여부 검사
        Thread.sleep(3000);

        assertTrue(future.isDone(), "실행 완료 여부 확인");
        assertDoesNotThrow(() -> future.get());
    }

    @Test
    public void testScheduleExceptionWithStatus() throws InterruptedException {
        Long postingId = 300L;
        LocalDateTime runAt = LocalDateTime.now().plusSeconds(2);

        ScheduledFuture<?> future = jobScheduler.schedule(
                postingId,
                runAt,
                () -> {
                    log.info("⚠️ 예외 작업 시작: {}", LocalDateTime.now());
                    throw new RuntimeException("의도된 테스트 예외");
                }
        );

        // 예약된 실행(2초) + 여유(1초)
        Thread.sleep(3_000);

        // 1) ExecutionException & CoreException 메시지 검증
        ExecutionException ex = assertThrows(
                ExecutionException.class,
                future::get,
                "future.get()에서 ExecutionException이 발생해야 합니다"
        );
        // (a) 실행 스레드 예외 메시지 안에 CoreException 클래스명이 포함되어 있는지
        String execMsg = ex.getMessage();
        assertTrue(
                execMsg.contains("CoreException"),
                "ExecutionException 메시지에 CoreException 이 포함되어야 합니다. 실제: " + execMsg
        );
        // (b) CoreException의 상세 메시지
        String causeMsg = ex.getCause().getMessage();
        assertEquals(
                "예약된 작업 실행 중 오류가 발생했습니다.",
                causeMsg,
                "CoreException.getMessage()가 기대한 메시지여야 합니다."
        );

        // 2) DB에 저장된 ScheduledJob 상태가 FAILED로 변경되었는지 검증
        ScheduledJob job = jobRepository.findAll().stream()
                .filter(j -> j.getPostingId().equals(postingId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("postingId=" + postingId + "인 Job이 없어졌습니다."));
        assertEquals(
                JobStatus.FAILED,
                job.getStatus(),
                "예외 발생 시 DB에 저장된 Job.status는 FAILED여야 합니다."
        );
    }
}

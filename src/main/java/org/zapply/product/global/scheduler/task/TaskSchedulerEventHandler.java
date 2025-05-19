package org.zapply.product.global.scheduler.task;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.zapply.product.global.scheduler.entity.ScheduledJob;
import org.zapply.product.global.scheduler.enumerate.JobStatus;
import org.zapply.product.global.scheduler.repository.ScheduledJobRepository;

import java.util.List;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TaskSchedulerEventHandler {
    private final ScheduledJobRepository jobRepo;
    private final SchedulingService schedulingService;

    /** 서버 시작 시, DB에 남은 SCHEDULED 상태 작업을 다시 등록 */
    @EventListener(ApplicationReadyEvent.class)
    public void reRegisterJobs() {
        List<ScheduledJob> pending = jobRepo.findAllByStatus(JobStatus.SCHEDULED);
        for (ScheduledJob job : pending) {
            long id = job.getId();
            LocalDateTime execAt = job.getExecuteAt();
            // action: 호출 로직(예: threadsPostingClient.createSingleMedia 등)
            schedulingService.scheduleTask(job.getPostingId(), execAt, () -> {
                // TODO: 실제 작업 호출
            });
        }
    }

    /** 예약 정보가 변경될 때 호출: 기존 job 삭제 후 재등록 */
    @EventListener
    public void handleUpdateSchedule(UpdateScheduleEvent event) {
        schedulingService.cancelTask(event.getJobId());
        schedulingService.scheduleTask(
                event.getPostingId(),
                event.getNewExecuteAt(),
                event.getAction()
        );
    }
}


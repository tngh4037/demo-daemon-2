package daemon.scheduled.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringJob {

    private final TaskScheduler taskScheduler;

    @Scheduled(cron = "${cron.MonitoringJob}")
    public void executeTask() {
        ThreadPoolTaskScheduler scheduler = (ThreadPoolTaskScheduler) taskScheduler;
        log.info("queue size: " + scheduler.getScheduledThreadPoolExecutor().getQueue().size());
    }
}

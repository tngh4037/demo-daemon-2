package daemon.scheduled.jobs.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventAlarmJob {

    @Scheduled(cron = "${cron.EventAlarmJob}")
    public void executeTask() {
        log.info(this.getClass().getSimpleName() + " start");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info(this.getClass().getSimpleName() + " end");
    }
}

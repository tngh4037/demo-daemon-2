package daemon.scheduled;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableAsync // Async 활성화
@EnableScheduling // 스케줄러 활성화
@SpringBootApplication
public class ScheduledApplication {

	@Value("${thread.pool.size}")
	private int POOL_SIZE;

	public static void main(String[] args) {
		SpringApplication.run(ScheduledApplication.class, args);
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(POOL_SIZE); // default: 1
		return taskScheduler;
	}

}

// ========================================================

// [ @Async 로 동작하는 스레드는 ThreadPoolTaskScheduler 스레드 풀에 의한 스레드인가 ? No ]
// 별도의 풀(ex. SimpleAsyncTaskExecutor, ScheduledThreadPoolExecutor, ...) 로 동작하는 것으로 보인다. 그리고 그 corePoolSize 는 8로 보인다.
// 8개까지 쓰레드가 수행되다가, 이후부터는 스레드가 더 만들어 지지는 않아보인다. 그리고 큐에 쌓이는 것 같다. ( queueCapacity : 21억쯤 )
// 참고) https://devpad.tistory.com/133 (하단 Async)

// ========================================================

// [ @EnableAsync - @Async 없는 경우 ] (큐사이즈): 2 -> 2 -> 2 -> 1 -> 1 -> 1 -> 1
// 1) 애플리케이션 시작 시점에 모든 Job들이 queue에 밀어넣어진다.
// 2) 각 job들에 설정된 지정 시간이 되면, 스레드 풀에 스레드가 해당 job을 꺼내와 실행한다. ( 그 스레드가 job을 점유하면서 실행 )
//   ㄴ 만약, 해당 job 수행 과정에서 뭔가 문제가 있어서 다음 수행시간이 도래하도록 끝마치지지 못했어도, 그 시간이 도래했을 때 다른 스레드가 해당 job을 실행시키지 않는다. ( 큐에 없음 )
//   ㄴ 또한, 실행을 마쳤는데, 마친 시간이 사실 그 사이 3번 더 수행됐어야 했어도, 수행을 마치고 그 세번을 연달아 수행하거나 하지 않는다.

// [ @EnableAsync - @Async 있는 경우 ] (큐사이즈): 2 -> 2 -> 2 -> 1 -> 2 -> 2 -> 2
// 1) 애플리케이션 시작 시점에 모든 Job들이 queue에 밀어넣어진다.
// 2) 각 job들에 설정된 지정 시간이 되면, 스레드 풀에 스레드가 해당 job을 꺼내와 실한다. ( -> 참고로 여기서 스레드는 ThreadPoolTaskScheduler 에서 관리되는 스레드가 아니다. ( ThreadPoolTaskExecutor ) ) ( 해당 스레드는 job을 수행하지만, job을 점유하지는 않고, job이 바로 다시 큐에 들어온다. )
//   ㄴ 만약 해당 job 수행 과정에서 뭔가 문제가 있어서 다음 수행시간이 도래하도록 끝마치지지 못했다면, 그 시간이 도래했을 때 다른 스레드가 해당 job을 실행시킨다. ( -> 참고로 여기서 스레드는 ThreadPoolTaskScheduler 에서 관리되는 스레드가 아니다. ( ThreadPoolTaskExecutor ) ) ( 이 또한 마찬가지로 해당 스레드가 job을 수행하지만, job을 점유하지는 않고, job이 바로 다시 큐에 들어온다. )

// 참고) https://docs.spring.io/spring-framework/reference/integration/scheduling.html
// 참고) https://devel-repository.tistory.com/47

// ========================================================


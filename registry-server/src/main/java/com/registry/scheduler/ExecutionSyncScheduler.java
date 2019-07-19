package com.registry.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by boozer on 2019. 6. 18
 */
@Component
public class ExecutionSyncScheduler {

    // 실행에 대한 결과 등과 같은 정보 동기화 모듈 (10분 마다)
    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void checkRunningCluster() {

    }
}

package com.example.quartz.configuration;

import com.example.quartz.quartzjob.BatchScheduledJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 스케줄 구성을 위한 두가지 할 일
 * 1. 쿼츠 잡의 빈 구성
 * 2. 트리거 생성
 */
@Configuration
public class QuartzConfiguration {

    /**
     * 잡 클래스를 전달 후, 잡을 수행할 트리거가 존재하지 않더라도 쿼츠가 해당 잡 정의를 삭제하지 않도록 JobDetail 생성
     * @return 실행할 쿼츠 잡 수행 시에 사용되는 메타데이터
     */
    @Bean
    public JobDetail quartzJobDetail() {
        return JobBuilder.newJob(BatchScheduledJob.class)
                .storeDurably().build();
    }

    /**
     * 5초마다 한 번씩 잡을 기동하는데 최초 한 번 수행 이후 4번 반복할 스케줄 정의
     * 쿼츠의 TriggerBuilder 를 사용해 새로운 트리거를 생성하면서 잡과 스케줄을 전달
     * @return
     */
    @Bean
    public Trigger trigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5).withRepeatCount(4);

        return TriggerBuilder.newTrigger()
                .forJob(quartzJobDetail())
                .withSchedule(scheduleBuilder)
                .build();
    }
}

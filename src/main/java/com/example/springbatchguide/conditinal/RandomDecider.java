package com.example.springbatchguide.conditinal;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.Random;

/**
 * 다음에 무엇을 해야 할지 프로그래밍적으로 결정
 * JobExecution 과 StepExecution 을 argument 로 전달받기 때문에, 모든 정보 사용 가능
 */
public class RandomDecider implements JobExecutionDecider {

    private Random random = new Random();

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

        if (random.nextBoolean()) {
            return new FlowExecutionStatus(FlowExecutionStatus.COMPLETED.getName());
        } else {
            return new FlowExecutionStatus(FlowExecutionStatus.FAILED.getName());
        }
    }
}

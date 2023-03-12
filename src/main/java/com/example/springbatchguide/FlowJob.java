package com.example.springbatchguide;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 잡에서 사용한 스텝의 정의를 추출해서 재사용 가능한 컴포넌트 형태로 만들 수 있다.
 *
 * 스텝의 순서를 외부화하는 세가지 방법
 * 1. 스텝의 시퀀스를 독자적인 플로우로 만드는 방법
 * 2. 플로우 스텝을 사용하는 방법
 * 3. 잡 내에서 다른 잡을 호출하는 방법
 */
@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class FlowJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    /**
     * 플로우 스텝을 사용하여 스텝을 외부화
     *
     * @return
     */
    @Bean
    public Job ConditionalStepLogicJob() {
        return this.jobBuilderFactory.get("conditionalStepLogicJob")
                .start(initializeBatch())
                .next(runBatch())
                .build();
    }

    /**
     * 플로우를 스텝으로 래핑하고 이 스텝을 잡 빌더로 전달할 수 있다.
     * 플로우스텝을 사용하면 스프링 배치는 해당 플로우가 담긴 스텝을 하나의 스텝처럼 기록하여,
     * 개별 스텝을 집계하지 않고도 플로우의 영향을 전체적으로 볼 수 있어 모니터링과 리포팅에 이점이 있다.
     * @return
     */
    @Bean
    public Step initializeBatch() {
        return this.stepBuilderFactory.get("initializeBatch")
                .flow(preProcessingFlow())
                .build();
    }

    @Bean
    public Step runBatch() {
        return this.stepBuilderFactory.get("runBatch")
                .tasklet(runBatchTasklet())
                .build();
    }

    @Bean
    public Flow preProcessingFlow() {
        return new FlowBuilder<Flow>("preProcessingFlow")
                .start(loadFileStep())
                .next(loadCustomerStep())
                .next(updateStartStep())
                .build();
    }

    @Bean
    public Step loadFileStep() {
        return this.stepBuilderFactory.get("loadFileStep")
                .tasklet(loadStockFile())
                .build();
    }

    @Bean
    public Step loadCustomerStep() {
        return this.stepBuilderFactory.get("loadCustomerStep")
                .tasklet(loadCustomerFile())
                .build();
    }

    @Bean
    public Step updateStartStep() {
        return this.stepBuilderFactory.get("updateStartStep")
                .tasklet(updateStart())
                .build();
    }

    @Bean
    public Tasklet loadStockFile() {
        return (stepContribution, chunkContext) -> {
            System.out.println("The stock file has been loaded");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet loadCustomerFile() {
        return (stepContribution, chunkContext) -> {
            System.out.println("The customer file has been loaded");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet updateStart() {
        return (stepContribution, chunkContext) -> {
            System.out.println("The start has been updated");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet runBatchTasklet() {
        return (stepContribution, chunkContext) -> {
            System.out.println("Then start has been updated");
            return RepeatStatus.FINISHED;
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(FlowJob.class, args);
    }
}

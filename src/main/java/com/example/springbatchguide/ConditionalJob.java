package com.example.springbatchguide;

import com.example.springbatchguide.conditinal.RandomDecider;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 스프링 배치의 조건로직(Conditional Logic) 사용하기 : 잡 흐름 커스터마이징
 */
@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class ConditionalJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    /**
     * firstStep 의 실행결과가 정상이면 이어서 successStep을 실행하며
     * firstStep 이 ExitStatus 로 FAILED 를 반환하면 failureStep 을 실행한다.
     *
     * on 메서드 : 스프링 배치가 스텝의 ExitStatus를 평가해 어떤 일을 수행할지 결정할 수 있도록 구성
     * @return
     */
    @Bean
    public Job job1() {
        return this.jobBuilderFactory.get("conditionalLogicJob")
                .start(firstStep())
                .on("FAILED").to(failureStep())
                .on("FAILED").fail() // Failed 상태로 잡 종료.
                .on("FAILED").stopAndRestart(successStep()) // 종료 후 다시 successStep 부터 다시 시작
                .from(firstStep()).on("*").to(successStep())
                .end() // Job 종료
                .build();

    }

    @Bean
    public Job job2() {
        return this.jobBuilderFactory.get("conditionalDeciderJob")
                .start(firstStep())
                .next(decider())
                .from(decider())
                .on("FAILED").to(failureStep())
                .from(decider())
                .on("*").to(successStep())
                .end()
                .build();

    }

    @Bean
    public JobExecutionDecider decider() {
        return new RandomDecider();
    }

    @Bean
    public Step firstStep() {
        return this.stepBuilderFactory.get("firstStep")
                .tasklet(passTasklet())
                .build();
    }

    @Bean
    public Step failureStep() {
        return this.stepBuilderFactory.get("failureStep")
                .tasklet(failureTasklet())
                .build();
    }

    @Bean
    public Step successStep() {
        return this.stepBuilderFactory.get("successStep")
                .tasklet(successTasklet())
                .build();
    }

    @Bean
    public Tasklet passTasklet() {
        return (stepContribution, chunkContext) -> RepeatStatus.FINISHED;
    }

    @Bean
    public Tasklet successTasklet() {
        return (stepContribution, chunkContext) -> {
            System.out.println("Success!");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet failureTasklet() {
        return (stepContribution, chunkContext) -> {
            System.out.println("Failure!");
            return RepeatStatus.FINISHED;
        };
    }
}

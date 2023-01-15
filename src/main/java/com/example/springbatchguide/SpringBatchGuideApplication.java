package com.example.springbatchguide;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class SpringBatchGuideApplication {

    private final JobBuilderFactory jobBuilderFactory; // 잡을 생성하는 빌더
    private final StepBuilderFactory stepBuilderFactory; // 스텝을 생성하는 빌더

    @Bean
    public Step step() {
        return this.stepBuilderFactory.get("step1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Hello, World");

                    // tasklet이 완료됐음을 스프링 배치에게 알림
                    // cf) RespeatStuats.CONTINUABLE : 다시 호출(재수행)
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchGuideApplication.class, args);
    }

}

package com.example.springbatchguide;

import com.example.springbatchguide.batch.DailyJobTimestamper;
import com.example.springbatchguide.batch.JobLoggerListener;
import com.example.springbatchguide.batch.ParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class SpringBatchGuideApplication {

    private final JobBuilderFactory jobBuilderFactory; // 잡을 생성하는 빌더
    private final StepBuilderFactory stepBuilderFactory; // 스텝을 생성하는 빌더

    /**
     * JobParameters 의 유효성 검증 수행
     * 여러 유효성 검증기를 구성하하려면 CompositeJobParametersValidator 사용
     * @return
     */
    @Bean
    public CompositeJobParametersValidator validator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

        // 스프링 배치에서 제공하는 유효성 검증기. 필수 파라미터가 누락없이 전달됐는지 확인
        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(
                new String[] {"fileName"}, new String[] {"name", "currentDate"}
        );

        defaultJobParametersValidator.afterPropertiesSet();;

        validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));

        return validator;
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .validator(validator())
                .incrementer(new DailyJobTimestamper())
                .listener(JobListenerFactoryBean.getListener(new JobLoggerListener()))
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .tasklet(helloWorldTasklet(null, null)).build();
    }

    /**
     * 늦은 바인딩 기능을 사용한 스텝 스코프 빈 구성
     * 스텝의 실행 범위(스탭 스코프)나 잡의 실행 범위(잡 스코프)에 들어갈 때까지 빈 생성을 지연함으로써
     * 명령행 또는 다른 소스에서 받아들인 잡 파라미터를 빈 생성 시점에 주입할 수 있다.
     * @param name
     * @return
     */
    @StepScope
    @Bean
    public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name,
                                     @Value("#{jobParameters['fileName']}") String fileName) {
        return (stepContribution, chunkContext) -> {
            System.out.println("Hello, " + name + "!");
            System.out.println("file name = " + fileName);
            return RepeatStatus.FINISHED;
        };
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringBatchGuideApplication.class, args);
    }

}

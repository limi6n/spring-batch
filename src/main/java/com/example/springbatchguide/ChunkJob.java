package com.example.springbatchguide;

import com.example.springbatchguide.chunk.RandomChunkSizePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableMBeanExport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class ChunkJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkBasedJob() {
        return this.jobBuilderFactory.get("chunkBasedJob")
                .start(chunkStep())
                .build();
    }

    @Bean
    public Step chunkStep() {
        return this.stepBuilderFactory.get("chunkStep()")
                // .<String, String> chunk(1000) // 커밋 간격을 하드코딩해 청크 크기를 결정
                // .<String, String> chunk(completionPolicy()) // 청크가 완료되는 시점을 프로그래밍 방식으로 정의
                .<String, String> chunk(randomChunkSizePolicy())
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    /**
     * CompositeCompletionPolicy : 청크 완료라고 판단된다면 해당 청크가 완료된 것으로 표시
     * TimeoutTerminationPolicy : 타임아웃 값을 구성하면, 청크 내에서 처리 시간이 해당 시간이 넘을 때 완료된 것으로 간주
     * SimpleCompletionPolicy : 처리된 아이템 개수를 세어서, 미리 구성해둔 임곗값에 도달하면 청크 완료로 표시
     * @return
     */
//    @Bean
//    public CompletionPolicy completionPolicy() {
//        CompositeCompletionPolicy policy = new CompositeCompletionPolicy();
//        policy.setPolicies(
//                new CompletionPolicy[] {
//                        new TimeoutTerminationPolicy(3),
//                        new SimpleCompletionPolicy(1000)
//                }
//        );
//        return policy;
//    }

    @Bean
    public CompletionPolicy randomChunkSizePolicy() {
        return new RandomChunkSizePolicy();
    }

    @Bean
    public ListItemReader<String> itemReader() {
        List<String> items = new ArrayList<>(100000);

        for(int i=0; i<100000; i++) {
            items.add(UUID.randomUUID().toString());
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(">> current item = " + item);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ChunkJob.class, args);
    }
}

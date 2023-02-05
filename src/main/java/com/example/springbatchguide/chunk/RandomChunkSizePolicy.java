package com.example.springbatchguide.chunk;

import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Random;

/**
 * CompletionPolicy 인터페이스 구현체
 * start -> update -> isComplete(context) -> isComplete(context, result)
 */
public class RandomChunkSizePolicy implements CompletionPolicy {

    private int chunkSize;
    private int totalProcessed;
    private Random random = new Random();

    /**
     * 청크 완료 여부의 상태를 기반으로 결정 로직을 수행
     * @param context
     * @param result
     * @return
     */
    @Override
    public boolean isComplete(RepeatContext context, RepeatStatus result) {

        if (RepeatStatus.FINISHED == result) {
            return true;
        } else {
            return isComplete(context);
        }
    }

    /**
     * 내부 상태를 이용해 청크 완료 여부를 판단
     * @param context
     * @return
     */
    @Override
    public boolean isComplete(RepeatContext context) {
        return this.totalProcessed >= chunkSize;
    }

    /**
     * 청크 시작 시 해당 구현체가 필요로 하는 모든 내부 상태를 초기화
     * @param repeatContext
     * @return
     */
    @Override
    public RepeatContext start(RepeatContext repeatContext) {
        this.chunkSize = random.nextInt(20);
        this.totalProcessed = 0;

        System.out.println("The chunk size has been set to " + this.chunkSize);
        return repeatContext;
    }

    /**
     * 각 아이템이 처리되면 update 메서드가 호출되어 내부 상태를 갱신
     * @param repeatContext
     */
    @Override
    public void update(RepeatContext repeatContext) {
        this.totalProcessed++;
    }
}

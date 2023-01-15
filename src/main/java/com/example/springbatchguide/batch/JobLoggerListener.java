package com.example.springbatchguide.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

/**
 * 생명주기에서 잡 실행과 관련 된 로직 추가 시 사용
 * BeforJob : 잡 실행 전에 준비해둬야할 뭔가가 있을 경우
 * AfterJob : 많은 잡이 실행 이후에 정리 작업을 수행할 경우 (예: 파일 삭제 또는 보관 등)
 * [작성방법 두가지]
 * 1. JobExecutionListener 인터페이스에서 제공하는 beforeJob 과 afterJob을 구현
 * 2. @BeforeJob @AfterJob 어노테이션을 사용
 * [주의사항]
 * 어노테이션을 사용할 경우 잡에 주입 시, 래핑을 해야한다.
 */
public class JobLoggerListener {
    private static String START_MESSAGE = "%s is beginning execution";
    private static String END_MESSAGE =
            "%s has completed with the status %s";

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println(String.format(START_MESSAGE,
                jobExecution.getJobInstance().getJobName()));
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        System.out.println(String.format(END_MESSAGE,
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus()));
    }
}

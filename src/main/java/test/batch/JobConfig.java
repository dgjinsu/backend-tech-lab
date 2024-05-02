package test.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobConfig {
    private int chunkSize = 5;

    @Bean
    public Job testJob(JobRepository jobRepository, Step testStep) {
        return new JobBuilder("testJob", jobRepository)
                .start(testStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean Step testStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("testStep", jobRepository)
                .<String, String>chunk(chunkSize, transactionManager)
                .reader(customItemReader())
                .processor(customItemProcessor())
                .writer(customItemWriter())
                .faultTolerant()
//                .skip(CustomException.class)
//                .skipLimit(3)
                .retry(CustomException.class)
                .retryLimit(2)
                .build();
    }

    @Bean
    public ItemReader<String> customItemReader() {
        return new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws SkipException {
                i++;
                System.out.println("itemReader : " + i);
                return i > 5 ? null : String.valueOf(i);
            }
        };
    }

    @Bean
    public ItemProcessor<? super String, String> customItemProcessor() {
        return item -> {
            if (item.equals("3")){
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "error");
            }

            System.out.println("itemProcessor : " + item);
            return item;
        };
    }

    @Bean
    public ItemWriter<? super String> customItemWriter() {
        return items -> {
            System.out.println("items : " + items);
        };
    }
}

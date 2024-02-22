package com.example.batch.job.parameter;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class JobParameterConfig {

    @Bean
    @JobScope
    public JobParameter jobParameter() {
        return new JobParameter();
    }
}

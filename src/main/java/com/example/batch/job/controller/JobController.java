package com.example.batch.job.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JobController {
    private final JobRegistry jobRegistry;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;

    @GetMapping("/restart")
    public String restartJob() throws Exception {
        for (String jobName : jobRegistry.getJobNames()) {
            JobInstance lastJobInstance = jobExplorer.getLastJobInstance(jobName);
            if (lastJobInstance != null) {
                JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
                if (lastJobExecution != null && BatchStatus.FAILED.equals(lastJobExecution.getStatus())) {
                    jobOperator.restart(lastJobExecution.getId());
                }
            }
        }
        return "restart job";
    }
}
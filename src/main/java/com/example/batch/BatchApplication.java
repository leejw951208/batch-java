package com.example.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class BatchApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BatchApplication.class, args);
		int exit = SpringApplication.exit(context);
		System.exit(exit);
	}
}

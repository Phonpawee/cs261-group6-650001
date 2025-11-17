package com.example.LoginTUgether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  
public class LogTUgetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogTUgetherApplication.class, args);
		System.out.println("✅ TUgether Application Started!");
		System.out.println("✅ Event Scheduler is ENABLED - Auto-closing expired events every hour");
	}

}









































































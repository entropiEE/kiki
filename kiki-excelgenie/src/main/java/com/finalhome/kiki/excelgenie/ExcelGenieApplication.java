package com.finalhome.kiki.excelgenie;

import com.finalhome.kiki.excelgenie.util.ContextUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ExcelGenieApplication implements CommandLineRunner {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ExcelGenieApplication.class, args);
		ContextUtil.setContext(context);
		ExcelGenie.execute();
	}

	@Override
	public void run(String... args) throws Exception {

	}
}

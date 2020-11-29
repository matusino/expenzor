package com.matus.expenzor;

import com.matus.expenzor.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@Import(SwaggerConfig.class)
@CrossOrigin(origins = "*")
public class ExpenzorApplication {


	public static void main(String[] args) {
		SpringApplication.run(ExpenzorApplication.class, args);
	}

}

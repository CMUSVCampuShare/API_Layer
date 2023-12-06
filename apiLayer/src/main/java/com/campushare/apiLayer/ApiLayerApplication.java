package com.campushare.apiLayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.campushare.apiLayer.config.WebConfig;

@SpringBootApplication
@Import(WebConfig.class)
public class ApiLayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiLayerApplication.class, args);
	}

}

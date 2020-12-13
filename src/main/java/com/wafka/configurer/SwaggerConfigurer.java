package com.wafka.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan("com.wafka.controller")
public class SwaggerConfigurer {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_12)
				.useDefaultResponseMessages(false)
				.apiInfo(apiEndPointsInfo())
				.select()
				.paths(PathSelectors.regex("/api/v1/.*"))
				.build();
	}

	private ApiInfo apiEndPointsInfo() {
		return new ApiInfoBuilder().title("wafka REST API")
				.description("API documentation to enable REST communication with Apache Kafka")
				.version("1.0.0").build();
	}
}

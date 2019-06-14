package edu.wlu.graffiti.swagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket searchApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("edu.wlu.graffiti.controller"))
				.paths(regex(".*csv|.*json|.*xml|/filter.*|/results.*|/graffito.*"))
				.build()
				.apiInfo(metaData());
	}
	
	@SuppressWarnings("deprecation")
	private ApiInfo metaData() {
		ApiInfo apiInfo = new ApiInfo(
				"AGP Search API",
				"REST API for Ancient Graffiti Project",
				"1.0",
				"Terms of Service",
				"Hammad Ahmad", 
				"Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License",
				"https://creativecommons.org/licenses/by-nc-sa/4.0/"
				);
		return apiInfo;
	}
}

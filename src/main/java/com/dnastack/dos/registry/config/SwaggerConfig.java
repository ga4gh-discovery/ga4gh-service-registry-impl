package com.dnastack.dos.registry.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("com.dnastack.dos.registry")
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("DOS Registry")
                .description("GA4GH Data-Object-Service (DOS) registry service")
                .contact(new Contact("DNAStack API Team", "http://dnastack.com", "apisupport@dnastack.com"))
                .version("1.0.0")
                .build();
    }
}


package com.taskmanager.taskmanager_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager REST API")
                        .version("1.0.0")
                        .description("""
                                A REST API for managing tasks.

                                Features include:
                                - Create, retrieve, update, and delete tasks.
                                - Paginate task results.
                                - Filter tasks by completion status.
                                - Validate task titles and descriptions.
                                """)
                        .contact(new Contact()
                                .name("Task Manager API Support"))
                        .license(new License()
                                .name("MIT License")));
    }
}

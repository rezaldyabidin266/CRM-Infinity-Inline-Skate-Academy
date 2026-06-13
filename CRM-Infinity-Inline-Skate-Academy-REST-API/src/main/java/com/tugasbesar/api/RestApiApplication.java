package com.tugasbesar.api;

import com.tugasbesar.app.database.DatabaseMigrator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

    @PostConstruct
    public void migrateDatabase() {
        DatabaseMigrator.migrate();
    }

    @Bean
    public CommandLineRunner printStartupInfo(Environment environment) {
        return args -> {
            String port = environment.getProperty("local.server.port", environment.getProperty("server.port", "8080"));
            System.out.println();
            System.out.println("REST API aktif di port: " + port);
            System.out.println("Swagger UI: http://localhost:" + port + "/swagger-ui.html");
            System.out.println();
        };
    }
}

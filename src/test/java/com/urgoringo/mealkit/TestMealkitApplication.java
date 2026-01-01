package com.urgoringo.mealkit;

import org.springframework.boot.SpringApplication;

public class TestMealkitApplication {

    static void main(String[] args) {
        SpringApplication.from(MealkitApplication::main).with(EmbeddedDatabaseConfiguration.class).run(args);
    }

}


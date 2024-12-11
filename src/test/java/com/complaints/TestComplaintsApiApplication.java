package com.complaints;

import org.springframework.boot.SpringApplication;

public class TestComplaintsApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(ComplaintsApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

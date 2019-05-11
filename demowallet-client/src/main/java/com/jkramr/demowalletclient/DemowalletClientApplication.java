package com.jkramr.demowalletclient;

import com.jkramr.demowalletclient.service.emulator.LoggingThreadedEmulatorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemowalletClientApplication implements CommandLineRunner {

    private final LoggingThreadedEmulatorService emulatorService;

    public DemowalletClientApplication(LoggingThreadedEmulatorService emulatorService) {
        this.emulatorService = emulatorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemowalletClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        emulatorService.start();
    }
}

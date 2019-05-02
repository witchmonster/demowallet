package com.jkramr.demowalletclient;

import com.jkramr.demowalletclient.service.EmulatorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemowalletClientApplication implements CommandLineRunner {

    public DemowalletClientApplication(EmulatorService emulator) {
        this.emulator = emulator;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemowalletClientApplication.class, args);
    }

    private EmulatorService emulator;

    @Override
    public void run(String... args) throws Exception {
        emulator.run();
    }
}

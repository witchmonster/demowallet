package com.jkramr.demowalletclient.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
@Component
public class EmulatorService {

    @Autowired
    public EmulatorService(RoundFactory roundFactory, GrpcChannel channel) {
        this.roundFactory = roundFactory;
        this.channel = channel;
    }

    private RoundFactory roundFactory;
    private GrpcChannel channel;

    @Value( "${demowallet.client.users}" )
    private Integer numberOfUsers;
    @Value( "${demowallet.client.concurrent_threads_per_user}" )
    private Integer threadsPerUser;
    @Value( "${demowallet.client.rounds_per_thread}" )
    private Integer roundsPerThread;

    public void run() {
        for (int i = 0; i < numberOfUsers; i++) {
            createTreadsForUser(i);
        }
    }

    private void createTreadsForUser(int userId) {
        for (int j = 0; j < threadsPerUser; j++) {
            ExecutorService executorService = Executors.newCachedThreadPool();
            executeRoundsPerThread(executorService, userId);
        }
    }

    private void executeRoundsPerThread(ExecutorService executorService, int userId) {
        executorService.execute(() -> {
            for (int i = 0; i < roundsPerThread; i++) {
                roundFactory.getRound(channel).run(userId);
            }
        });
    }
}

package com.jkramr.demowalletclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EmulatorService {

    @Autowired
    public EmulatorService(RoundFactory roundFactory, GrpcChannel channel) {
        this.roundFactory = roundFactory;
        this.channel = channel;
    }

    private RoundFactory roundFactory;
    private GrpcChannel channel;

    @Value("${demowallet.client.users}")
    private Integer numberOfUsers;
    @Value("${demowallet.client.concurrent_threads_per_user}")
    private Integer threadsPerUser;
    @Value("${demowallet.client.rounds_per_thread}")
    private Integer roundsPerThread;
    @Value("${demowallet.client.grpc_channel}")
    private String grpcChannel;

    public void run() {
        if (grpcChannel.equals("local")) {
            createUsersAndExecuteRounds();
        }
    }

    private void createUsersAndExecuteRounds() {
        //setting up users in single thread, alternatively could have been pre-setup by SQL inserts
        registerAll();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 1; i <= numberOfUsers; i++) {
            createTreadsForUser(i, executorService);
        }
    }

    private void registerAll() {
        for (int i = 1; i <= numberOfUsers; i++) {
            channel.register(i);
        }
    }

    private void createTreadsForUser(int userId, ExecutorService executorService) {
        for (int j = 0; j < threadsPerUser; j++) {
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

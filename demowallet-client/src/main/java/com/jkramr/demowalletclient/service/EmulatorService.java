package com.jkramr.demowalletclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EmulatorService {

    public static final String DATE_FORMAT = "YYYY-MM-dd hh:mm:ss SSS";
    private Logger logger = LoggerFactory.getLogger(EmulatorService.class);

    @Autowired
    public EmulatorService(RoundFactory roundFactory, TestGrpcChannel channel) {
        this.roundFactory = roundFactory;
        this.channel = channel;
    }

    private RoundFactory roundFactory;
    private TestGrpcChannel channel;

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
            logger.info("Opening channel LOCAL");
            createUsersAndExecuteRounds();
        }
    }

    private void createUsersAndExecuteRounds() {
        //out of scope setup: registering users before task execution, alternatively could be pre-setup by SQL inserts
        ExecutorService executorService = Executors.newCachedThreadPool();
        registerAll(executorService);

        executeTasks(executorService);
    }

    private void registerAll(ExecutorService executorService) {
        logger.info("User count: {}. Pre-registering users...", numberOfUsers);
        List<Callable<Boolean>> threads = new ArrayList<>();
        for (int i = 1; i <= numberOfUsers; i++) {
            addRegisterThread(threads, i);
        }
        try {
            executorService.invokeAll(threads);
        } catch (InterruptedException e) {
            executorService.shutdown();
        }
        logger.info("Register complete.");
    }

    private void executeTasks(ExecutorService executorService) {
        logger.info("Starting thread execution for {} users, {} threads per user, {} rounds per thread.", numberOfUsers, threadsPerUser, roundsPerThread);
        LocalDateTime startTime = LocalDateTime.now();
        logger.info("Started at {}", startTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        List<Callable<Boolean>> threads = new ArrayList<>();
        for (int i = 1; i <= numberOfUsers; i++) {
            addThreadsPerUser(threads, i);
        }
        try {
            executorService.invokeAll(threads);
        } catch (InterruptedException e) {
            executorService.shutdown();
        }
        executorService.shutdown();
        LocalDateTime endTime = LocalDateTime.now();
        logger.info("Completed at {}", endTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        logger.info("{} API calls executed in {} concurrent threads in {} milliseconds", channel.getCallCount(), numberOfUsers * threadsPerUser, Duration.between(startTime, endTime).toMillis());
    }

    private void addThreadsPerUser(List<Callable<Boolean>> threads, int userId) {
        for (int j = 0; j < threadsPerUser; j++) {
            logger.debug("Executing thread {} for user {}", j, userId);
            threads.add(createRounds(userId));
        }
    }

    private void addRegisterThread(List<Callable<Boolean>> threads, int i) {
        threads.add(() -> {
            channel.register(i);
            return true;
        });
    }

    private Callable<Boolean> createRounds(int userId) {
        return () -> {
            for (int i = 0; i < roundsPerThread; i++) {
                roundFactory.getRound(channel).run(userId);
            }
            return true;
        };
    }
}

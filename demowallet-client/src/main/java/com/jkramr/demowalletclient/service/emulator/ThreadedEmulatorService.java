package com.jkramr.demowalletclient.service.emulator;

import com.jkramr.demowalletclient.service.grpc.GrpcChannel;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadedEmulatorService<T extends GrpcChannel> implements EmulatorService {

    public static final String DATE_FORMAT = "YYYY-MM-dd hh:mm:ss SSS";
    public static final String GRPC_CHANNEL_LOCAL = "local";

    public ThreadedEmulatorService(RoundEmulatorFactory roundFactory, T channel) {
        this.roundFactory = roundFactory;
        this.channel = channel;
    }

    private final RoundEmulatorFactory roundFactory;
    final T channel;

    @Value("${demowallet.client.users}")
    protected Integer numberOfUsers;
    @Value("${demowallet.client.concurrent_threads_per_user}")
    protected Integer threadsPerUser;
    @Value("${demowallet.client.rounds_per_thread}")
    protected Integer roundsPerThread;
    @Value("${demowallet.client.grpc_channel}")
    protected String grpcChannel;

    @Override
    public void start() {
        if (grpcChannel.equals(GRPC_CHANNEL_LOCAL)) {
            createUsersAndExecuteRounds();
        }
    }

    private void createUsersAndExecuteRounds() {
        //out of scope setup: registering users before task execution, alternatively could be pre-setup by SQL inserts
        ExecutorService executorService = Executors.newCachedThreadPool();
        registerAll(executorService);

        executeTasks(executorService);
    }

    protected void registerAll(ExecutorService executorService) {
        List<Callable<Boolean>> threads = new ArrayList<>();
        for (int i = 1; i <= numberOfUsers; i++) {
            addRegisterThread(threads, i);
        }
        try {
            executorService.invokeAll(threads);
        } catch (InterruptedException e) {
            executorService.shutdown();
        }
    }

    protected void executeTasks(ExecutorService executorService) {
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
    }

    private void addThreadsPerUser(List<Callable<Boolean>> threads, int userId) {
        for (int j = 0; j < threadsPerUser; j++) {
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

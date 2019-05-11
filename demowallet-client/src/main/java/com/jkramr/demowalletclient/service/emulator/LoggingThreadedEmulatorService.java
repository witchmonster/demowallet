package com.jkramr.demowalletclient.service.emulator;

import com.jkramr.demowalletclient.service.grpc.LocalGrpcChannel;
import com.jkramr.demowalletclient.service.grpc.LoggingGrpcChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class LoggingThreadedEmulatorService extends ThreadedEmulatorService<LoggingGrpcChannel> {

    private Logger logger = LoggerFactory.getLogger(LoggingThreadedEmulatorService.class);

    @Value("${demowallet.client.perfinfo}")
    private boolean shouldLogCalls;

    public LoggingThreadedEmulatorService(RoundEmulatorFactory roundFactory, LocalGrpcChannel channel) {
        super(roundFactory, new LoggingGrpcChannel(channel));
    }

    @Override
    protected void registerAll(ExecutorService executorService) {
        logger.info("User count: {}. Pre-registering users...", numberOfUsers);
        super.registerAll(executorService);
        logger.info("Registration complete.");
    }

    @Override
    protected void executeTasks(ExecutorService executorService) {
        logger.info("Starting thread execution for {} users, {} threads per user, {} rounds per thread.", numberOfUsers, threadsPerUser, roundsPerThread);
        LocalDateTime startTime = LocalDateTime.now();
        logger.info("Started at {}", startTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        super.executeTasks(executorService);
        LocalDateTime endTime = LocalDateTime.now();
        logger.info("Completed at {}", endTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        Duration runDuration = Duration.between(startTime, endTime);
        logger.info("Finished in {} milliseconds", runDuration.toMillis());
        if (shouldLogCalls) {
            logger.info("------ PERFORMANCE INFO ------");
            AtomicLong callCount = channel.getCallCount();
            int concurrentThreads = numberOfUsers * threadsPerUser;
            logger.info("{} API calls executed in {} concurrent threads in {} milliseconds", callCount.get(), concurrentThreads, runDuration.toMillis());
            double callsPerSecond = callCount.get() * 1000 / runDuration.toMillis();
            logger.info("{} API calls per second in {} concurrent threads.", callsPerSecond, concurrentThreads);
        }
    }
}

package com.jkramr.demowalletclient.service.emulator;

import com.jkramr.demowalletapi.model.Common;
import com.jkramr.demowalletclient.service.grpc.GrpcChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RoundEmulatorFactory {

    private Logger logger = LoggerFactory.getLogger(LoggingThreadedEmulatorService.class);

    Round getRound(GrpcChannel channel) {
        int random = (int) (Math.random() * 3) + 1;
        switch (random) {
            case 1: return (userId) -> {
                logger.debug("Running RoundA for user {}", userId);
                channel.deposit(userId, Common.Currency.USD, 100);
                channel.withdraw(userId, Common.Currency.USD, 200);
                channel.deposit(userId, Common.Currency.EUR, 100);
                channel.getBalance(userId);
                channel.withdraw(userId, Common.Currency.USD, 100);
                channel.getBalance(userId);
                channel.withdraw(userId, Common.Currency.USD, 100);
            };
            case 2: return (userId -> {
                logger.debug("Running RoundB for user {}", userId);
                channel.withdraw(userId, Common.Currency.GBP, 100);
                channel.deposit(userId, Common.Currency.GBP, 300);
                channel.withdraw(userId, Common.Currency.GBP, 100);
                channel.withdraw(userId, Common.Currency.GBP, 100);
                channel.withdraw(userId, Common.Currency.GBP, 100);
            });
            case 3:
            default: return (userId) -> {
                logger.debug("Running RoundC for user {}", userId);
                channel.getBalance(userId);
                channel.deposit(userId, Common.Currency.USD, 100);
                channel.deposit(userId, Common.Currency.USD, 100);
                channel.withdraw(userId, Common.Currency.USD, 100);
                channel.deposit(userId, Common.Currency.USD, 100);
                channel.getBalance(userId);
                channel.withdraw(userId, Common.Currency.USD, 200);
                channel.getBalance(userId);
            };
        }
    }

    public interface Round {
        void run(int userId);
    }
}

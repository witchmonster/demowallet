package com.jkramr.demowalletclient.service;

import com.jkramr.demowalletapi.model.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RoundFactory {

    private Logger logger = LoggerFactory.getLogger(EmulatorService.class);

    Round getRound(TestGrpcChannel channel) {
        int random = (int) (Math.random() * 3) + 1;
        switch (random) {
            case 1: return new RoundA(channel);
            case 2: return new RoundB(channel);
            case 3:
            default: return new RoundC(channel);
        }
    }

    public class RoundA extends Round {

        RoundA(TestGrpcChannel channel) {
            super(channel);
        }

        public void run(int userId) {
            logger.debug("Running RoundA for user {}", userId);
            channel.deposit(userId, Common.Currency.USD, 100);
            channel.withdraw(userId, Common.Currency.USD, 200);
            channel.deposit(userId, Common.Currency.EUR, 100);
            channel.getBalance(userId);
            channel.withdraw(userId, Common.Currency.USD, 100);
            channel.getBalance(userId);
            channel.withdraw(userId, Common.Currency.USD, 100);
        }
    }

    public class RoundB extends Round {

        RoundB(TestGrpcChannel channel) {
            super(channel);
        }

        public void run(int userId) {
            logger.debug("Running RoundB for user {}", userId);
            channel.withdraw(userId, Common.Currency.GBP, 100);
            channel.deposit(userId, Common.Currency.GBP, 300);
            channel.withdraw(userId, Common.Currency.GBP, 100);
            channel.withdraw(userId, Common.Currency.GBP, 100);
            channel.withdraw(userId, Common.Currency.GBP, 100);
        }
    }

    public class RoundC extends Round {

        RoundC(TestGrpcChannel channel) {
            super(channel);
        }

        public void run(int userId) {
            logger.debug("Running RoundC for user {}", userId);
            channel.getBalance(userId);
            channel.deposit(userId, Common.Currency.USD, 100);
            channel.deposit(userId, Common.Currency.USD, 100);
            channel.withdraw(userId, Common.Currency.USD, 100);
            channel.deposit(userId, Common.Currency.USD, 100);
            channel.getBalance(userId);
            channel.withdraw(userId, Common.Currency.USD, 200);
            channel.getBalance(userId);
        }
    }

    public abstract class Round {
        Round(TestGrpcChannel channel) {
            this.channel = channel;
        }

        TestGrpcChannel channel;

        public abstract void run(int userId);
    }
}

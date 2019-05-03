package com.jkramr.demowalletclient.service;

import com.jkramr.demowalletapi.model.Common;
import org.springframework.stereotype.Component;

@Component
public class RoundFactory {

    Round getRound(GrpcChannel channel) {
        int random = (int) (Math.random() * 3) + 1;
        switch (random) {
            case 1: return new RoundA(channel);
            case 2: return new RoundB(channel);
            case 3:
            default: return new RoundC(channel);
        }
    }

    public class RoundA extends Round {

        RoundA(GrpcChannel channel) {
            super(channel);
        }

        public void run(int userId) {
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

        RoundB(GrpcChannel channel) {
            super(channel);
        }

        public void run(int userId) {
            channel.withdraw(userId, Common.Currency.GBP, 100);
            channel.deposit(userId, Common.Currency.GBP, 300);
            channel.withdraw(userId, Common.Currency.GBP, 100);
            channel.withdraw(userId, Common.Currency.GBP, 100);
            channel.withdraw(userId, Common.Currency.GBP, 100);
        }
    }

    public class RoundC extends Round {

        RoundC(GrpcChannel channel) {
            super(channel);
        }

        public void run(int userId) {
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
        Round(GrpcChannel channel) {
            this.channel = channel;
        }

        GrpcChannel channel;

        public abstract void run(int userId);
    }
}

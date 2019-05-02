package com.jkramr.demowalletclient.service;

import com.jkramr.demowalletapi.model.Common;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class RoundFactory {

    public Round getRound(GrpcChannel channel) {
        int random = (int) (Math.random() * 3) + 1;
        switch (random) {
            case 1: return new RoundA(channel);
            case 2: return new RoundB(channel);
            case 3:
            default: return new RoundC(channel);
        }
    }

    public class RoundA extends Round {

        public RoundA(GrpcChannel channel) {
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

        public RoundB(GrpcChannel channel) {
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

        public RoundC(GrpcChannel channel) {
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

    @Data
    public abstract class Round {
        public Round(GrpcChannel channel) {
            this.channel = channel;
        }

        protected GrpcChannel channel;

        public abstract void run(int userId);
    }
}

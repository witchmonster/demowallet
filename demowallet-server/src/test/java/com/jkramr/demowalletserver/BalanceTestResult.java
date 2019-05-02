package com.jkramr.demowalletserver;

import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletapi.model.Common;
import lombok.Data;
import org.junit.Assert;

@Data
class BalanceTestResult {
    private Balance.BalanceResponse response;

    BalanceTestResult(Balance.BalanceResponse response) {
        this.response = response;
    }

    BalanceValidator forCurrency(Common.Currency currency) {
        return new BalanceValidator(getBalanceInfoForCurrency(currency));
    }

    private Balance.WalletBalance getBalanceInfoForCurrency(Common.Currency currency) {
        return response.getBalanceInfoList().stream()
                .filter(walletBalance -> currency.equals(walletBalance.getCurrency()))
                .findAny()
                .orElse(null);
    }

    class BalanceValidator {
        private Balance.WalletBalance balanceInfoForCurrency;

        public BalanceValidator(Balance.WalletBalance balanceInfoForCurrency) {
            this.balanceInfoForCurrency = balanceInfoForCurrency;
        }

        public BalanceAssertableTestResult amountEquals(double amount) {
            Assert.assertNotNull(balanceInfoForCurrency);
            Assert.assertEquals(amount, balanceInfoForCurrency.getAmount(), 0);
            return parent();
        }
    }

    private BalanceAssertableTestResult parent() {
        return new BalanceAssertableTestResult(response);
    }

    class BalanceAssertableTestResult extends BalanceTestResult {
        public BalanceAssertableTestResult(Balance.BalanceResponse response) {
            super(response);
        }

        public void assertBalances() {
        }
    }
}

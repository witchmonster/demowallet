package com.jkramr.demowalletserver.systemdata;

import com.jkramr.demowalletapi.grpc.Balance;
import com.jkramr.demowalletapi.grpc.Deposit;
import com.jkramr.demowalletapi.model.Common;
import org.junit.Assert;

public class BalanceTestResult {
    private Balance.BalanceResponse response;

    public BalanceTestResult(Balance.BalanceResponse response) {
        this.response = response;
    }

    public CurrencyBalanceValidator forCurrency(Common.Currency currency) {
        Assert.assertEquals(Balance.BalanceResponse.ResponseCode.OK, response.getCode());
        return new CurrencyBalanceValidator(getBalanceInfoForCurrency(currency));
    }

    private Balance.BalanceInfo getBalanceInfoForCurrency(Common.Currency currency) {
        return response.getBalanceInfoList().stream()
                .filter(walletBalance -> currency.equals(walletBalance.getCurrency()))
                .findAny()
                .orElse(null);
    }

    public void assertResponseIsEmpty() {
        Assert.assertTrue(response.getBalanceInfoList().isEmpty());
    }

    public class CurrencyBalanceValidator {
        private Balance.BalanceInfo balanceInfoForCurrency;

        CurrencyBalanceValidator(Balance.BalanceInfo balanceInfoForCurrency) {
            this.balanceInfoForCurrency = balanceInfoForCurrency;
        }

        public BalanceTestResult amountEquals(double amount) {
            Assert.assertNotNull(balanceInfoForCurrency);
            Assert.assertEquals(amount, balanceInfoForCurrency.getAmount(), 0);
            return getParent();
        }
    }

    private BalanceTestResult getParent() {
        return this;
    }
}

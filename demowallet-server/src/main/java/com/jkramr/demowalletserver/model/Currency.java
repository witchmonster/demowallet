package com.jkramr.demowalletserver.model;

import com.jkramr.demowalletapi.model.Common;

public enum Currency {
    EUR, USD, GBP;

    public static Currency of(Common.Currency currency) {
        return Currency.valueOf(currency.name());
    }

    public static Common.Currency to(Currency currency) {
        return Common.Currency.valueOf(currency.name());
    }
}

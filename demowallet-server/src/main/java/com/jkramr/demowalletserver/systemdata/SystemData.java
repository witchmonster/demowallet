package com.jkramr.demowalletserver.systemdata;

import com.jkramr.demowalletserver.model.Currency;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SystemData {

    public Optional<Currency> getCurrency(String currency) {
        return Arrays.stream(Currency.values())
                .filter(c -> currency.equals(c.name()))
                .findAny();
    }

    public List<Currency> getAllCurrencies() {
        return Arrays.asList(Currency.values());
    }

}

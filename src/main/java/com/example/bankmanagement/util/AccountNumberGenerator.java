package com.example.bankmanagement.util;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class AccountNumberGenerator {

    private static final Random random = new Random();
    private static final int ACCOUNT_NUMBER_LENGTH = 10;

    public String generate() {
        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
package com.example.bankcards.util;

import java.security.SecureRandom;

public final class CardGenerator {

    private static final SecureRandom random = new SecureRandom();

    private static final int CARD_NUMBER_LENGTH = 16;

    private static final long VISA_PREFIX = 4L;
    private static final long MASTERCARD_PREFIX = 5L;


    public static String generateCardNumber() {
        long prefix = random.nextBoolean() ? VISA_PREFIX : MASTERCARD_PREFIX;
        return generateCardNumberWithPrefix(prefix);
    }

    public static String generateCardNumberWithPrefix(long prefix) {
        StringBuilder number = new StringBuilder();
        number.append(prefix);

        for (int i = 0; i < CARD_NUMBER_LENGTH - 2; i++) {
            number.append(random.nextInt(10));
        }

        int checksum = calculateLuhnChecksum(number.toString());
        number.append(checksum);

        return number.toString();
    }

    private static int calculateLuhnChecksum(String partialNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = partialNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(partialNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit % 10 + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        int checksum = (10 - (sum % 10)) % 10;
        return checksum;
    }
}

package com.example.backendlotte.global.util;

public final class PersonalDataMasker {

    private PersonalDataMasker() {
    }

    public static String maskName(
            String name
    ) {
        if (name == null || name.isBlank()) {
            return "";
        }

        String value = name.trim();

        if (value.length() == 1) {
            return "*";
        }

        if (value.length() == 2) {
            return value.charAt(0) + "*";
        }

        return value.charAt(0)
            + "*".repeat(value.length() - 2)
            + value.charAt(value.length() - 1);
    }

    public static String maskBirthDate(
            java.time.LocalDate birthDate
    ) {
        if (birthDate == null) {
            return "";
        }

        return birthDate.getYear()
            + "-**-**";
    }

    public static String maskPhone(
            String phone
    ) {
        if (phone == null || phone.isBlank()) {
            return "";
        }

        String digits =
            phone.replaceAll("[^0-9]", "");

        if (digits.length() < 7) {
            return "****";
        }

        String first =
            digits.substring(0, 3);

        String last =
            digits.substring(
                digits.length() - 4
            );

        return first + "-****-" + last;
    }
}
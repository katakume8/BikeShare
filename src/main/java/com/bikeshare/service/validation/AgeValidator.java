package com.bikeshare.service.validation;

import java.time.LocalDate;

import com.bikeshare.service.auth.BankIDService;

/**
 * Age validation using Swedish personal number and BankID auth.
 * Mirrors the flow/order from the provided AlcoholPurchaseValidator example,
 * but checks for age >= 18.
 */
public class AgeValidator {

    private final IDNumberValidator idNumberValidator;
    private final BankIDService bankIDService;

    public AgeValidator(IDNumberValidator idNumberValidator, BankIDService bankIDService) {
        this.idNumberValidator = idNumberValidator;
        this.bankIDService = bankIDService;
    }

    /**
     * Returns true if the person is 18 or older, else false.
     * Throws IllegalArgumentException on invalid ID or failed auth, as per example flow.
     */
    public boolean isAdult(String idNumber) {
        if (!idNumberValidator.isValidIDNumber(idNumber)) {
            throw new IllegalArgumentException("Invalid ID number");
        }

        if (!bankIDService.authenticate(idNumber)) {
            throw new IllegalArgumentException("Authentication failed");
        }

        int birthYear = Integer.parseInt(idNumber.substring(0, 4));
        int birthMonth = Integer.parseInt(idNumber.substring(4, 6));
        int birthDay = Integer.parseInt(idNumber.substring(6, 8));

        LocalDate birthDate = LocalDate.of(birthYear, birthMonth, birthDay);
        LocalDate today = LocalDate.now();

        int age = today.getYear() - birthDate.getYear();
        if (today.isBefore(birthDate.plusYears(age))) {
            age--;
        }
        return age >= 18;
    }
}

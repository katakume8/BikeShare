package com.bikeshare.service.validation.impl;

import com.bikeshare.service.auth.BankIDService;
import com.bikeshare.service.validation.AgeCheck;
import com.bikeshare.service.validation.AgeValidator;
import com.bikeshare.service.validation.IDNumberValidator;

/**
 * Correct reference implementation using {@link AgeValidator}.
 */
public class CorrectAgeCheck implements AgeCheck {
    @Override
    public boolean isAdult(String idNumber, boolean authenticated) {
    IDNumberValidator idv = s -> s != null && s.matches("\\d{12}");
        BankIDService bank = pn -> authenticated;
        return new AgeValidator(idv, bank).isAdult(idNumber);
    }
}

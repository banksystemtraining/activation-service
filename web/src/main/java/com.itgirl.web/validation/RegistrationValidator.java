package com.itgirl.web.validation;

import com.itgirl.web.dto.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationValidator {

    public void validate(RegistrationRequest registrationRequest) throws ValidatorException {
        if (!registrationRequest.password1().equals(registrationRequest.password2())) {
            throw new ValidatorException("Password doesn't match.");
        }
    }
}
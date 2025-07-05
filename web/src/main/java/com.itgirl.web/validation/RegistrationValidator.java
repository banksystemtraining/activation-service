package com.itgirl.web.validation;

import com.itgirl.web.dto.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationValidator {

    public void validate(RegistrationRequest registrationRequest) throws ValidatorException {
        log.debug("Validating registration request for email: {}", registrationRequest.email());
        if (!registrationRequest.password1().equals(registrationRequest.password2())) {
            log.warn("Passwords mismatch");
            throw new ValidatorException("Password doesn't match.");
        }
        log.debug("Registration request validation passed for email:{}", registrationRequest.email());
    }
}
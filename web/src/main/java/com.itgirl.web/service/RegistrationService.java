package com.itgirl.web.service;

import com.itgirl.web.client.UserCoreClient;
import com.itgirl.web.dto.RegistrationRequest;
import com.itgirl.web.dto.UserCreateRequest;
import com.itgirl.web.exception.ValidationException;
import com.itgirl.web.validation.RegistrationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserCoreClient userCoreClient;
    private final RegistrationValidator registrationValidator;

    public void register(RegistrationRequest registrationRequest, BindingResult bindingResult) throws ValidatorException {
        log.info("Start registration user with email: {}", registrationRequest.email());
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors detected");
            throw new ValidationException(formatValidationErrors(bindingResult));
        }
        log.debug("Performing custom validation");
        registrationValidator.validate(registrationRequest);
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                registrationRequest.name(),
                registrationRequest.surname(),
                registrationRequest.email(),
                registrationRequest.password1()
        );
        log.debug("Creating user via UserCoreClient");
        userCoreClient.createUser(userCreateRequest);
        log.info("Successfully registered user with email: {}", registrationRequest.email());
    }

    private String formatValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream().map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).collect(Collectors.joining("; "));
    }
}
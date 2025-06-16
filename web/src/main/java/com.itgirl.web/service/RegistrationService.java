package com.itgirl.web.service;

import com.itgirl.web.client.UserCoreClient;
import com.itgirl.web.dto.RegistrationRequest;
import com.itgirl.web.dto.UserCreateRequest;
import com.itgirl.web.exception.ValidationException;
import com.itgirl.web.validation.RegistrationValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserCoreClient userCoreClientWeb;
    private final RegistrationValidator registrationValidator;

    public void register(RegistrationRequest registrationRequest, BindingResult bindingResult) throws ValidatorException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(formatValidationErrors(bindingResult));
        }

        registrationValidator.validate(registrationRequest);
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                registrationRequest.name(),
                registrationRequest.surname(),
                registrationRequest.email(),
                registrationRequest.password1()
        );
        userCoreClientWeb.createUser(userCreateRequest);
    }

    private String formatValidationErrors(BindingResult bindingResult){
        return bindingResult.getFieldErrors().stream().map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).collect(Collectors.joining("; "));
    }
}
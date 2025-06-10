package com.itgirl.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivationMessage {
    private String email;
    private String name;
    private String password;
    private String activationKey;
}

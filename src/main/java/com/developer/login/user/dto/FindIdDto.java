package com.developer.login.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindIdDto {
    @NotBlank
    private String email;
}

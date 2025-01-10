package com.grabduck.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDto {
    private String token;
    private String type = "Bearer";
    
    public AuthenticationResponseDto(String token) {
        this.token = token;
    }
}

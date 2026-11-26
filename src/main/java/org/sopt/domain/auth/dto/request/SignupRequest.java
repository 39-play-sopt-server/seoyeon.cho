package org.sopt.domain.auth.dto.request;

public record SignupRequest(
        String name,
        String email,
        String password
) {}

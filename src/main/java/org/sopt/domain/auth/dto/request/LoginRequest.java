package org.sopt.domain.auth.dto.request;

public record LoginRequest(
        String email,
        String password
) {}

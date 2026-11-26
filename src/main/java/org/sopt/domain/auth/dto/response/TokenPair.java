package org.sopt.domain.auth.dto.response;

public record TokenPair(String accessToken, String refreshToken) {
}

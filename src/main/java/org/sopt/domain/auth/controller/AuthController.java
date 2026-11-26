package org.sopt.domain.auth.controller;

import java.time.Duration;
import org.sopt.domain.auth.dto.response.TokenPair;
import org.sopt.domain.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sopt.domain.auth.dto.request.SignupRequest;
import org.sopt.domain.auth.dto.request.LoginRequest;
import org.sopt.domain.auth.dto.response.LoginResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final long refreshTokenExpiration;
    private final boolean cookieSecure;

    public AuthController(
            AuthService authService,
            @Value("${jwt.refresh-token-expiration}")
            long refreshTokenExpiration,
            @Value("${jwt.cookie-secure:true}")
            boolean cookieSecure
    ) {
        this.authService = authService;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request.name(), request.email(), request.password()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        TokenPair tokens = authService.login(request.email(), request.password());

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofMillis(refreshTokenExpiration))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(tokens.accessToken()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(
            @CookieValue(value = "refreshToken", required = false)
            String refreshToken
    ) {
        // RefreshToken 쿠키 없는 경우
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 존재하지 않습니다.");
        }

        // Access/RefreshToken 재발급
        TokenPair tokens = authService.reissue(refreshToken);

        // 새 RefreshToken 쿠키 설정
        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofMillis(refreshTokenExpiration))
                .build();

        // AccessToken은 body로 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(tokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            Authentication authentication,
            @CookieValue(value = "refreshToken", required = false)
            String refreshToken
    ) {
        // SecurityContext에서 인증된 회원 id 추출
        Long memberId = (Long) authentication.getPrincipal();

        // RefreshToken 무효화
        authService.logout(memberId, refreshToken);

        // RefreshToken 쿠키 삭제
        ResponseCookie expiredCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ZERO)  // 쿠키 유효기간 0 설정 --> 브라우저에 저장된 RefreshToken 즉시 제거 요청
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }
}

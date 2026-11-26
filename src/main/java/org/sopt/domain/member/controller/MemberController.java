package org.sopt.domain.member.controller;

import java.time.Duration;
import org.sopt.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService service;
    private final boolean cookieSecure;

    public MemberController(
            MemberService service,
            @Value("${jwt.cookie-secure:true}")
            boolean cookieSecure
    ) {
        this.service = service;
        this.cookieSecure = cookieSecure;
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<Long> me(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberId);
    }

    // 회원 탈퇴 - Soft Delete
    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal Long memberId) {
        // 회원 탈퇴 처리
        service.withdraw(memberId);

        // RefreshToken 쿠키 삭제
        ResponseCookie expiredCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }
}

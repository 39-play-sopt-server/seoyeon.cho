package org.sopt.domain.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.sopt.domain.member.entity.Member;
import org.sopt.global.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenHasher refreshTokenHasher;
    private final JwtProvider jwtProvider;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenHasher refreshTokenHasher,
            JwtProvider jwtProvider
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenHasher = refreshTokenHasher;
        this.jwtProvider = jwtProvider;
    }

    // RefreshToken 발급 및 저장
    public String issue(Member member) {
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        Claims claims = jwtProvider.parseClaims(refreshToken);

        RefreshToken savedToken = new RefreshToken(
                member,
                refreshTokenHasher.hash(refreshToken),
                claims.getExpiration().toInstant()
        );

        refreshTokenRepository.save(savedToken);
        return refreshToken;
    }

    // RefreshToken 검증 및 회원 id 추출
    public Long validate(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw unauthorized("Refresh Token이 존재하지 않습니다.");
        }

        try {
            return jwtProvider.getRefreshTokenMemberId(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw unauthorized("유효하지 않은 Refresh Token입니다.");
        }
    }

    // DB 저장된 유효한 RefreshToken 조회
    public RefreshToken findValidToken(String refreshToken, Long memberId) {
        String tokenHash = refreshTokenHasher.hash(refreshToken);

        RefreshToken savedToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> unauthorized("등록되지 않은 Refresh Token입니다."));

        if (savedToken.isExpired()
                || !savedToken.getMember().getId().equals(memberId)
                || savedToken.getMember().isDeleted()) {
            throw unauthorized("유효하지 않은 Refresh Token입니다.");
        }

        return savedToken;
    }

    // RefreshToken 삭제
    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    // 재발급 시 기존 토큰 삭제
    public void deleteAndFlush(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.flush();
    }

    private ResponseStatusException unauthorized(String message) {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
    }
}

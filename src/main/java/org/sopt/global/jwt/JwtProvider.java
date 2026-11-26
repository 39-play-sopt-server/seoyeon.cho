package org.sopt.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // AccessToken 발급
    public String createAccessToken(Long memberId) {
        return createToken(memberId, "ACCESS", accessTokenExpiration);
    }

    // RefreshToken 발급
    public String createRefreshToken(Long memberId) {
        return createToken(memberId, "REFRESH", refreshTokenExpiration);
    }

    // 공통 JWT 발급
    public String createToken(Long memberId, String tokenType, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))   // 누구의 토큰인지
                .claim("tokenType", tokenType)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)                       // 발급 시각
                .expiration(new Date(now.getTime() + expiration)) // 만료 시각
                .signWith(secretKey)                 // 서명
                .compact();
    }

    // JWT 검증 & Claims 파싱
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)   // 서명, 만료 검증 → 실패 시 JwtException
                .getPayload();
    }

    // AccessToken에서 id 추출
    public Long getMemberId(String token) {
        Claims claims = parseClaims(token);

        if (!"ACCESS".equals(claims.get("tokenType", String.class))) {
            throw new IllegalArgumentException("Access Token이 아닙니다.");
        }
        return Long.valueOf(claims.getSubject());
    }

    // RefreshToken에서 id 추출
    public Long getRefreshTokenMemberId(String token) {
        Claims claims = parseClaims(token);

        if (!"REFRESH".equals(claims.get("tokenType", String.class))) {
            throw new IllegalArgumentException("Refresh Token이 아닙니다.");
        }
        return Long.valueOf(claims.getSubject());
    }
}

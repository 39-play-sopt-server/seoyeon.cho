package org.sopt.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.UUID;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";

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
        return createToken(memberId, TokenType.ACCESS, accessTokenExpiration);
    }

    // RefreshToken 발급
    public String createRefreshToken(Long memberId) {
        return createToken(memberId, TokenType.REFRESH, refreshTokenExpiration);
    }

    // 공통 JWT 발급
    private String createToken(Long memberId, TokenType tokenType, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))   // 누구의 토큰인지
                .claim(TOKEN_TYPE_CLAIM, tokenType.name())
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
                .parseSignedClaims(token)   // 서명, 만료 검증 -> 실패 시 JwtException
                .getPayload();
    }

    // AccessToken에서 id 추출
    public Long getMemberId(String token) {
        return getMemberIdByType(token, TokenType.ACCESS);
    }

    // RefreshToken에서 id 추출
    public Long getRefreshTokenMemberId(String token) {
        return getMemberIdByType(token, TokenType.REFRESH);
    }

    // 토큰 타입 검증 및 회원 ID 추출
    private Long getMemberIdByType(String token, TokenType expectedType) {
        Claims claims = parseClaims(token);

        String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);

        if (!expectedType.name().equals(tokenType)) {
            throw new JwtException("올바르지 않은 토큰 타입입니다.");
        }

        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new JwtException("올바르지 않은 회원 ID입니다.", e);
        }
    }
}

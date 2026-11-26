package org.sopt.domain.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.sopt.domain.auth.dto.response.TokenPair;
import org.sopt.domain.auth.token.RefreshToken;
import org.sopt.domain.auth.token.RefreshTokenHasher;
import org.sopt.domain.auth.token.RefreshTokenRepository;
import org.sopt.global.jwt.JwtProvider;
import org.sopt.domain.member.entity.Member;
import org.sopt.domain.member.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenHasher refreshTokenHasher;

    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenHasher refreshTokenHasher) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenHasher = refreshTokenHasher;
    }

    @Transactional
    public Long signup(String name, String email, String password) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = new Member(name, email, passwordEncoder.encode(password));
        return memberRepository.save(member).getId();
    }

    @Transactional
    public TokenPair login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(member.getId());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        Claims claims = jwtProvider.parseClaims(refreshToken);
        String tokenHash = refreshTokenHasher.hash((refreshToken));

        RefreshToken savedToken = new RefreshToken(member, tokenHash, claims.getExpiration().toInstant());
        refreshTokenRepository.save(savedToken);

        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    public TokenPair reissue(String refreshToken) {
        // RefreshToken 유효성 검증
        Long memberId;

        try {
            memberId = jwtProvider.getRefreshTokenMemberId(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다.");
        }

        // 토큰 해시 처리
        String tokenHash = refreshTokenHasher.hash((refreshToken));

        // DB의 RefreshToken 조회
        RefreshToken savedToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "등록되지 않은 Refresh Token입니다."));

        // DB 만료 시간 및 회원 id 확인
        if (savedToken.isExpired() || !savedToken.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료되었거나 유효하지 않은 Refresh Token입니다.");
        }

        // 기존 RefreshToken 삭제
        refreshTokenRepository.delete(savedToken);
        refreshTokenRepository.flush();

        // 새 Access/RefreshToken 발급
        String newAccessToken = jwtProvider.createAccessToken(memberId);
        String newRefreshToken = jwtProvider.createRefreshToken(memberId);

        // 새 RefreshToken 해시, 만료 시간 저장
        Claims claims = jwtProvider.parseClaims(newRefreshToken);

        RefreshToken newSavedToken = new RefreshToken(
                savedToken.getMember(),
                refreshTokenHasher.hash(newRefreshToken),
                claims.getExpiration().toInstant()
        );

        refreshTokenRepository.save(newSavedToken);

        // 새로운 토큰 반환
        return new TokenPair(newAccessToken, newRefreshToken);
    }
}

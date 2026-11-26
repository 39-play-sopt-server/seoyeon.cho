package org.sopt.domain.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.sopt.domain.auth.dto.response.TokenPair;
import org.sopt.domain.auth.token.RefreshToken;
import org.sopt.domain.auth.token.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;


    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenService refreshTokenService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
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

        if (member.isDeleted() || !passwordEncoder.matches(password, member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(member.getId());
        String refreshToken = refreshTokenService.issue(member);

        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    public TokenPair reissue(String refreshToken) {
        // RefreshToken 유효성 검증
        Long memberId = refreshTokenService.validate(refreshToken);

        // DB 저장된 토큰 확인
        RefreshToken savedToken = refreshTokenService.findValidToken(refreshToken, memberId);

        Member member = savedToken.getMember();

        // 기존 RefreshToken 삭제
        refreshTokenService.deleteAndFlush(savedToken);

        // 새로운 Access/RefreshToken 발급
        String newAccessToken = jwtProvider.createAccessToken(memberId);
        String newRefreshToken = refreshTokenService.issue(member);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long memberId, String refreshToken) {
        // RefreshToken 검증
        Long refreshMemberId = refreshTokenService.validate(refreshToken);

        // Access/RefreshToken의 회원 id 비교
        if (!memberId.equals(refreshMemberId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰의 회원 정보가 일치하지 않습니다.");
        }

        // DB에 저장된 토큰 확인
        RefreshToken savedToken = refreshTokenService.findValidToken(refreshToken, memberId);

        // RefreshToken 삭제
        refreshTokenService.delete(savedToken);
    }
}

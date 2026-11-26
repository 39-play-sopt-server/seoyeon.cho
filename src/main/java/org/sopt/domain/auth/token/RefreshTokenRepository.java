package org.sopt.domain.auth.token;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // 재발급 시 등록 토큰 확인
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // 로그아웃, 토큰 교체 시 삭제
    void deleteByTokenHash(String tokenHash);
    // 회원 탈퇴 시 모든 기기 토큰 삭제
    void deleteAllByMemberId(Long memberId);
}

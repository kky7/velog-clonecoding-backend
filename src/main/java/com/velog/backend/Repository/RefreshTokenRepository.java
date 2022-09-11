package com.velog.backend.Repository;

import com.velog.backend.entity.Member;
import com.velog.backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByMember(Member member);
}

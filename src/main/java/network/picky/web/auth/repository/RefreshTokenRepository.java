package network.picky.web.auth.repository;

import network.picky.web.auth.domain.RefreshToken;
import network.picky.web.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    public boolean findByRefreshToken(String refreshToken);
}

package network.picky.web.auth.repository;

import network.picky.web.auth.domain.SavedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedTokenRepository extends JpaRepository<SavedToken, Long> {
    SavedToken findByRefreshToken(String refreshToken);
}

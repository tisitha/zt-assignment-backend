package com.tisitha.zephyra_tech_assignment.repository;

import com.tisitha.zephyra_tech_assignment.model.RefreshToken;
import com.tisitha.zephyra_tech_assignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByTokenHash(String hash);

    Optional<RefreshToken> findByTokenHashAndUser(String hash, User user);

}

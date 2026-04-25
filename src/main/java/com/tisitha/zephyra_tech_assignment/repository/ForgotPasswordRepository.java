package com.tisitha.zephyra_tech_assignment.repository;

import com.tisitha.zephyra_tech_assignment.model.ForgotPassword;
import com.tisitha.zephyra_tech_assignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword,Long> {

    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

    Optional<ForgotPassword> findByUserId(UUID id);
}

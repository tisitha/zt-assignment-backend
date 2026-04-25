package com.tisitha.zephyra_tech_assignment.service;

import com.tisitha.zephyra_tech_assignment.model.Provider;
import com.tisitha.zephyra_tech_assignment.model.RefreshToken;
import com.tisitha.zephyra_tech_assignment.model.Role;
import com.tisitha.zephyra_tech_assignment.model.User;
import com.tisitha.zephyra_tech_assignment.repository.RefreshTokenRepository;
import com.tisitha.zephyra_tech_assignment.repository.UserRepository;
import com.tisitha.zephyra_tech_assignment.util.JWTUtil;
import com.tisitha.zephyra_tech_assignment.util.TokenHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    public CustomOAuth2SuccessHandler(JWTUtil jwtUtil, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Assert.notNull(oAuth2User,"oAuth2User must not be null");
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFname(oAuth2User.getAttribute("given_name"));
                    newUser.setLname(oAuth2User.getAttribute("family_name"));
                    newUser.setProvider(Provider.GOOGLE);
                    newUser.setProfile(oAuth2User.getAttribute("picture"));
                    newUser.setRole(Role.ROLE_USER);
                    return userRepository.save(newUser);
                });

        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());

        RefreshToken refreshTokenData = new RefreshToken();
        refreshTokenData.setTokenHash(TokenHashUtil.hash(refreshToken));
        refreshTokenData.setUser(user);
        refreshTokenData.setExpiryTime(Instant.now().plus(Duration.ofDays(60)));
        refreshTokenRepository.save(refreshTokenData);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/c")
                .maxAge(60 * 60 * 24 * 60)
                .sameSite("none")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        getRedirectStrategy().sendRedirect(request, response, frontendUrl+"account/google/callback?code="+accessToken);
    }
}
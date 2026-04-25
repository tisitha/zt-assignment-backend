package com.tisitha.zephyra_tech_assignment.service;

import com.tisitha.zephyra_tech_assignment.dto.*;
import com.tisitha.zephyra_tech_assignment.exception.EmailTakenException;
import com.tisitha.zephyra_tech_assignment.exception.PasswordNotMatchException;
import com.tisitha.zephyra_tech_assignment.exception.AuthenticationFailedException;
import com.tisitha.zephyra_tech_assignment.exception.UserNotFoundException;
import com.tisitha.zephyra_tech_assignment.model.Provider;
import com.tisitha.zephyra_tech_assignment.model.RefreshToken;
import com.tisitha.zephyra_tech_assignment.model.Role;
import com.tisitha.zephyra_tech_assignment.model.User;
import com.tisitha.zephyra_tech_assignment.repository.RefreshTokenRepository;
import com.tisitha.zephyra_tech_assignment.repository.UserRepository;
import com.tisitha.zephyra_tech_assignment.util.JWTUtil;
import com.tisitha.zephyra_tech_assignment.util.TokenHashUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public AccountResponseDto getUser(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UserNotFoundException::new);
        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setId(user.getId());
        accountResponseDto.setFname(user.getFname());
        accountResponseDto.setLname(user.getLname());
        accountResponseDto.setEmail(user.getEmail());
        accountResponseDto.setRole(user.getRole());
        accountResponseDto.setProfile(user.getProfile());
        accountResponseDto.setProvider(user.getProvider());
        return accountResponseDto;
    }

    @Override
    public void registerUserAccount(UserRegisterDto userRegisterDto) {
        if(!userRegisterDto.getPassword().equals(userRegisterDto.getPasswordRepeat())){
            throw new PasswordNotMatchException();
        }
        if(userRepository.existsByEmail(userRegisterDto.getEmail())){
            throw new EmailTakenException();
        }
        User user = new User();
        user.setFname(userRegisterDto.getFname());
        user.setLname(userRegisterDto.getLname());
        user.setEmail(userRegisterDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setProvider(Provider.LOCAL);
        userRepository.save(user);
    }

    @Override
    public LoginResponseDto loginAccount(LoginDto loginDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(UserNotFoundException::new);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));
        if(!authentication.isAuthenticated()){
            throw new AuthenticationFailedException("Invalid username or password");
        }
        String refreshToken = jwtUtil.generateRefreshToken(loginDto.getEmail());
        String accessToken = jwtUtil.generateAccessToken(loginDto.getEmail());

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setId(user.getId());
        loginResponseDto.setName(user.getFname());
        loginResponseDto.setAccessToken(accessToken);
        loginResponseDto.setRole(user.getRole().name());
        loginResponseDto.setEmail(user.getEmail());
        loginResponseDto.setProvider(user.getProvider());

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
                .sameSite("strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return loginResponseDto;
    }

    @Override
    public LoginResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        User user = userRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);

        RefreshToken oldRefreshTokenData = refreshTokenRepository.findByTokenHashAndUser(TokenHashUtil.hash(refreshToken),user).orElseThrow(AuthenticationFailedException::new);
        if(oldRefreshTokenData.isRevoked() || oldRefreshTokenData.getExpiryTime().isBefore(Instant.now())){
            throw new AuthenticationFailedException("Invalid refresh token");
        }
        oldRefreshTokenData.setRevoked(true);
        refreshTokenRepository.save(oldRefreshTokenData);

        if (jwtUtil.validateToken(refreshToken, userDetails)) {

            String newAccessToken = jwtUtil.generateAccessToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);

            LoginResponseDto loginResponseDto = new LoginResponseDto();
            loginResponseDto.setId(user.getId());
            loginResponseDto.setName(user.getFname());
            loginResponseDto.setAccessToken(newAccessToken);
            loginResponseDto.setRole(user.getRole().name());
            loginResponseDto.setEmail(user.getEmail());
            loginResponseDto.setProvider(user.getProvider());

            RefreshToken refreshTokenData = new RefreshToken();
            refreshTokenData.setTokenHash(TokenHashUtil.hash(newRefreshToken));
            refreshTokenData.setUser(user);
            refreshTokenData.setExpiryTime(Instant.now().plus(Duration.ofDays(60)));
            refreshTokenRepository.save(refreshTokenData);

            ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/api/auth/c")
                    .maxAge(60 * 60 * 24 * 60)
                    .sameSite("strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return loginResponseDto;
        }
        throw new AuthenticationFailedException();
    }

    @Override
    public void logoutAccount(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }else{
            throw new AuthenticationFailedException("No cookies");
        }

        if (refreshToken == null) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        RefreshToken oldRefreshTokenData = refreshTokenRepository.findByTokenHash(TokenHashUtil.hash(refreshToken)).orElseThrow(AuthenticationFailedException::new);
        oldRefreshTokenData.setRevoked(true);
        refreshTokenRepository.save(oldRefreshTokenData);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/c")
                .maxAge(0)
                .sameSite("strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Assert.notNull(user,"user must not be null");
        Optional.ofNullable(userUpdateDTO.getFname()).ifPresent(user::setFname);
        Optional.ofNullable(userUpdateDTO.getLname()).ifPresent(user::setLname);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(NewPasswordRequestDto newPasswordRequestDto, Authentication authentication) {
        if(!authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentication.getName(),newPasswordRequestDto.getCurrentPassword())).isAuthenticated()){
            throw new AuthenticationFailedException();
        }
        User user = (User) authentication.getPrincipal();
        if(!newPasswordRequestDto.getPassword().equals(newPasswordRequestDto.getPasswordRepeat())){
            throw new PasswordNotMatchException();
        }
        Assert.notNull(user,"user must not be null");
        user.setPassword(passwordEncoder.encode(newPasswordRequestDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(PasswordDTO passwordDTO, Authentication authentication) {
        if(!authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentication.getName(),passwordDTO.password())).isAuthenticated()){
            throw new AuthenticationFailedException();
        }
        User user = (User) authentication.getPrincipal();
        Assert.notNull(user,"user must not be null");
        userRepository.deleteById(user.getId());
    }
}

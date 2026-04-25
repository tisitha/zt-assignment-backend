package com.tisitha.zephyra_tech_assignment.service;

import com.tisitha.zephyra_tech_assignment.dto.ChangePasswordDto;
import com.tisitha.zephyra_tech_assignment.dto.Mailbody;
import com.tisitha.zephyra_tech_assignment.exception.*;
import com.tisitha.zephyra_tech_assignment.model.ForgotPassword;
import com.tisitha.zephyra_tech_assignment.model.Provider;
import com.tisitha.zephyra_tech_assignment.model.User;
import com.tisitha.zephyra_tech_assignment.repository.ForgotPasswordRepository;
import com.tisitha.zephyra_tech_assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImp implements ForgotPasswordService{

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void verifyEmail(String email){
        User user = userRepository.findByEmailAndProvider(email, Provider.LOCAL).orElseThrow(UserNotFoundException::new);
        int otp = otpGenerator();
        Mailbody mailbody = Mailbody.builder()
                .to(email)
                .text("This is the OPT for your forgot password request "+otp)
                .subject("OTP for forgot password request")
                .build();
        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis()+60*1000*5))
                .user(user)
                .build();
        emailService.sendSimpleMessage(mailbody);
        forgotPasswordRepository.save(fp);
    }

    @Override
    public void verifyOtp(Integer otp,String email){
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp,user).orElseThrow(OtpNotFoundException::new);
        if (fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getId());
            throw new OtpNotFoundException();
        }
    }

    @Override
    @Transactional
    public void changePasswordHandler(ChangePasswordDto changePasswordDto, Integer otp, String email){
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp,user).orElseThrow(OtpNotFoundException::new);
        if (fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getId());
            throw new OtpExpiredException();
        }
        if(!Objects.equals(changePasswordDto.password(),changePasswordDto.repeatPassword())){
            throw new PasswordNotMatchException();
        }
        String encodedPassword = passwordEncoder.encode(changePasswordDto.password());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        forgotPasswordRepository.deleteById(fp.getId());

    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100000,999999);
    }
}
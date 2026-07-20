package com.scar.jobflow_backend.auth;

import com.scar.jobflow_backend.auth.dto.ForgotPasswordRequest;
import com.scar.jobflow_backend.auth.dto.ResetPasswordRequest;
import com.scar.jobflow_backend.auth.dto.VerifyResetCodeRequest;
import com.scar.jobflow_backend.auth.verification.TokenType;
import com.scar.jobflow_backend.auth.verification.VerificationTokenService;
import com.scar.jobflow_backend.common.exception.BadRequestException;
import com.scar.jobflow_backend.common.mail.MailService;
import com.scar.jobflow_backend.user.User;
import com.scar.jobflow_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.email()).orElse(null);

        if (user == null)
            return;

        String code = verificationTokenService.generateCode(user, TokenType.PASSWORD_RESET);
        mailService.sendPasswordResetCode(user.getEmail(), code);

    }

    public void verifyResetCode(VerifyResetCodeRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or code"));

        verificationTokenService.checkCode(user, TokenType.PASSWORD_RESET, request.code());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or code"));

        verificationTokenService.validateCode(user, TokenType.PASSWORD_RESET, request.code());

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

}

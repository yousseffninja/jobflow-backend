package com.scar.jobflow_backend.auth;

import com.scar.jobflow_backend.auth.dto.ConfirmEmailRequest;
import com.scar.jobflow_backend.auth.dto.RegisterRequest;
import com.scar.jobflow_backend.auth.verification.TokenType;
import com.scar.jobflow_backend.auth.verification.VerificationTokenService;
import com.scar.jobflow_backend.common.exception.BadRequestException;
import com.scar.jobflow_backend.common.mail.MailService;
import com.scar.jobflow_backend.security.AuthenticationService;
import com.scar.jobflow_backend.user.Role;
import com.scar.jobflow_backend.user.User;
import com.scar.jobflow_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;
    private final AuthenticationService authenticationService;

    @Transactional
    public TokenIssueResult register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("An account with this email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .emailVerified(false)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        String code = verificationTokenService.generateCode(savedUser, TokenType.EMAIL_VERIFICATION);
        mailService.sendVerificationCode(savedUser.getEmail(), code);

        return authenticationService.issueTokensFor(savedUser);
    }

    @Transactional
    public void confirmEmail(ConfirmEmailRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or code"));

        if (user.isEmailVerified()) {
            throw new BadRequestException("This email is already verified");
        }

        verificationTokenService.validateCode(user, TokenType.EMAIL_VERIFICATION, request.code());

        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
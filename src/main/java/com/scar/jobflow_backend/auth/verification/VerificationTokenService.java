package com.scar.jobflow_backend.auth.verification;

import com.scar.jobflow_backend.common.exception.BadRequestException;
import com.scar.jobflow_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 15;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String generateCode(User user, TokenType tokenType) {

        String rawCode = generateNumericCode();

        VerificationToken token = VerificationToken.builder()
                .user(user)
                .codeHash(passwordEncoder.encode(rawCode))
                .type(tokenType)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES))
                .build();

        tokenRepository.save(token);

        return rawCode;
    }

    @Transactional
    public void validateCode(User user, TokenType tokenType, String rawCode) {

        VerificationToken token = tokenRepository
                .findTopByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), tokenType)
                .orElseThrow(() -> new BadRequestException("No verification code found. Please request a new one."));

        if (token.isUsed()) {
            throw new BadRequestException("This code has already been used. Please request a new one.");
        }

        if (token.isExpired()) {
            throw new BadRequestException("This code has expired. Please request a new one.");
        }

        if (!passwordEncoder.matches(rawCode, token.getCodeHash())) {
            throw new BadRequestException("Invalid verification code.");
        }

        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

    }

    private String generateNumericCode() {
        int code = RANDOM.nextInt((int) Math.pow(10, CODE_LENGTH));
        return String.format("%0" + CODE_LENGTH + "d", code);
    }

}

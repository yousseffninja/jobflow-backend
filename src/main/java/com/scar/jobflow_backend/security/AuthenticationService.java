package com.scar.jobflow_backend.security;

import com.scar.jobflow_backend.auth.TokenIssueResult;
import com.scar.jobflow_backend.auth.dto.AuthResponse;
import com.scar.jobflow_backend.auth.dto.LoginRequest;
import com.scar.jobflow_backend.auth.dto.UserSummary;
import com.scar.jobflow_backend.common.exception.BadRequestException;
import com.scar.jobflow_backend.security.JwtService;
import com.scar.jobflow_backend.security.RefreshToken;
import com.scar.jobflow_backend.security.RefreshTokenRepository;
import com.scar.jobflow_backend.security.TokenHasher;
import com.scar.jobflow_backend.user.User;
import com.scar.jobflow_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;

    @Transactional
    public TokenIssueResult login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        return issueTokensFor(user);
    }

    @Transactional
    public TokenIssueResult refresh(String rawRefreshToken) {

        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is missing");
        }

        if (!jwtService.isTokenValid(rawRefreshToken)
                || !"REFRESH".equals(jwtService.extractTokenType(rawRefreshToken))) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String tokenHash = tokenHasher.hash(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadRequestException("Invalid or expired refresh token"));

        if (!storedToken.isValid()) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        // Rotation: invalidate the old token before issuing new ones
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return issueTokensFor(storedToken.getUser());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }

        String tokenHash = tokenHasher.hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional
    public TokenIssueResult issueTokensFor(User user) {

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String rawRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        storeRefreshToken(user, rawRefreshToken);

        UserSummary userSummary = new UserSummary(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isEmailVerified()
        );

        AuthResponse authResponse = new AuthResponse(accessToken, userSummary);
        return new TokenIssueResult(authResponse, rawRefreshToken);
    }

    private void storeRefreshToken(User user, String rawRefreshToken) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHasher.hash(rawRefreshToken))
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
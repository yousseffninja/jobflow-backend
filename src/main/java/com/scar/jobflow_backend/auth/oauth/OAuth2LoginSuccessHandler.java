package com.scar.jobflow_backend.auth.oauth;

import com.scar.jobflow_backend.auth.TokenIssueResult;
import com.scar.jobflow_backend.security.AuthenticationService;
import com.scar.jobflow_backend.user.Role;
import com.scar.jobflow_backend.user.User;
import com.scar.jobflow_backend.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Value("${jobflow.oauth.frontend-redirect-url}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .fullName(fullName != null ? fullName : email)
                    .passwordHash(null)
                    .emailVerified(true) // Google already verified this email
                    .role(Role.USER)
                    .provider("GOOGLE")
                    .build();
            return userRepository.save(newUser);
        });

        TokenIssueResult result = authenticationService.issueTokensFor(user);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.rawRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String encodedToken = URLEncoder.encode(result.authResponse().accessToken(), StandardCharsets.UTF_8);
        response.sendRedirect(frontendRedirectUrl + "?accessToken=" + encodedToken);
    }
}
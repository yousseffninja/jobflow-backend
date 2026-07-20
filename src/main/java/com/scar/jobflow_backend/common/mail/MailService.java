package com.scar.jobflow_backend.common.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String toMail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toMail);
        message.setSubject("Verify your JobFlow account");
        message.setText("Your verification code is: " + code + "\n\nThis code expires in 15 minutes.");
        mailSender.send(message);
    }

    public void sendPasswordResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your JobFlow password");
        message.setText("Your password reset code is: " + code + "\n\nThis code expires in 15 minutes.\n\nIf you didn't request this, you can safely ignore this email.");
        mailSender.send(message);
    }

}

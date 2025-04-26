package com.developer.login.config.email;

import com.developer.login.user.domain.PasswordResetToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Async
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${frontend.url}")
    private String frontendBaseUrl;

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        String link = frontendBaseUrl + "/verify-email?token=" + token;
        String htmlContent = "<div style='font-family:Arial, sans-serif; line-height:1.6; color:#333;'>"
                + "<h2>Hello,</h2>"
                + "<p>To complete your registration, please click the button below to verify your email address.</p>"
                + "<p>Email verification is required for security reasons. Once verified, you will have full access to all features.</p>"
                + "<p style='text-align:center; margin:20px 0;'><a href='" + link + "' style='display:inline-block; padding:12px 24px; color:#fff; background-color:#007BFF; text-decoration:none; border-radius:5px;'>Verify Email</a></p>"
                + "<p>If the button above doesn't work, please click the link below or copy and paste it into your browser:</p>"
                + "<p><a href='" + link + "'>" + link + "</a></p>"
                + "<p>This is an automated email, please do not reply.</p>"
                + "<p>Thank you,<br>Smart Lineup Team</p>"
                + "</div>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("[Login-springboot] Email Verification");

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("[Login-springboot] Password Reset Verification Code");

        String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                + "<h2 style='color: #007BFF;'>Smart Lineup</h2>"
                + "<p>Hello,</p>"
                + "<p>Please enter the verification code below to reset your password.</p>"
                + "<div style='margin: 20px 0; padding: 10px; background-color: #f0f0f0; border-radius: 5px; text-align: center; font-size: 1.2em; font-weight: bold;'>"
                + token
                + "</div>"
                + "<p>If you did not request a password reset, you can safely ignore this email.</p>"
                + "<p>Thank you.</p>"
                + "</div>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}

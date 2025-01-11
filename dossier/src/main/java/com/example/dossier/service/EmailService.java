package com.example.dossier.service;

import com.example.deal.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailMessage.getAddress());
        message.setSubject(emailMessage.getTheme().name());
        message.setText(emailMessage.getText());

        try {
            mailSender.send(message);
            log.info("Email sent to: {}", emailMessage.getAddress());
        } catch (Exception e) {
            log.error("Error sending email to: {}", emailMessage.getAddress(), e);
        }
    }
}

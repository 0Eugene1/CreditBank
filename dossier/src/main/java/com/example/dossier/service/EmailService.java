package com.example.dossier.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.enums.ThemeEnum;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailMessage emailMessage) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Устанавливаем получателя
            helper.setTo(emailMessage.getAddress());

            // Устанавливаем тему сообщения в зависимости от темы из emailMessage
            helper.setSubject(getSubjectByTheme(emailMessage.getTheme()));

            // Устанавливаем текст сообщения в зависимости от темы
            helper.setText(getTextByTheme(emailMessage.getTheme(), emailMessage));

            // Добавляем файл как вложение, если он есть
            String documentPath = emailMessage.getText();  // Путь к файлу
            if (documentPath != null && !documentPath.isEmpty()) {
                File documentFile = new File(documentPath);
                if (documentFile.exists()) {
                    helper.addAttachment(documentFile.getName(), documentFile);  // Добавляем файл как вложение
                } else {
                    log.error("File not found: {}", documentPath);
                }
            }

            // Отправляем письмо
            mailSender.send(message);
            log.info("Email sent to: {}", emailMessage.getAddress());

        } catch (MessagingException e) {
            log.error("Error sending email to: {}", emailMessage.getAddress(), e);
        }
    }

    // Получаем тему сообщения в зависимости от типа темы
    private String getSubjectByTheme(ThemeEnum theme) {
        return switch (theme) {
            case FINISH_REGISTRATION -> "Регистрация завершена";
            case CREATE_DOCUMENTS -> "Документы создаются";
            case SEND_DOCUMENTS -> "Документы отправлены";
            case CREDIT_ISSUED -> "Кредит выдан";
            case STATEMENT_DENIED -> "Заявление отклонено";
            case SEND_SES -> "SES код отправлен";
        };
    }

    // Получаем текст сообщения в зависимости от типа темы
    private String getTextByTheme(ThemeEnum theme, EmailMessage emailMessage) {
        return switch (theme) {
            case FINISH_REGISTRATION -> "Регистрация завершена";
            case CREATE_DOCUMENTS -> "Документы создаются для statementId: " + emailMessage.getStatementId();
            case SEND_DOCUMENTS -> "Документы отправлены для statementId: " + emailMessage.getStatementId();
            case CREDIT_ISSUED -> "Кредит выдан для statementId: " + emailMessage.getStatementId();
            case STATEMENT_DENIED -> "Заявление отклонено для statementId: " + emailMessage.getStatementId();
            case SEND_SES -> "Ваш Ses Code - " + emailMessage.getText();
        };
    }
}

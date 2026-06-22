package com.systeminfo;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;

import java.io.File;
import java.util.Properties;

/**
 * Отправщик email с прикреплённым HTML-отчётом.
 *
 * ⚠ ВАЖНО: Для работы нужно настроить SMTP отправителя!
 * См. инструкцию ниже.
 */
public class EmailSender {

    // ⚠ НАСТРОЙТЕ ЭТИ ПАРАМЕТРЫ!
    // Инструкция: https://support.google.com/accounts/answer/185833
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SENDER_EMAIL = "your_email@gmail.com";
    private static final String SENDER_APP_PASSWORD = "your_app_password";

    /**
     * Отправляет HTML-отчёт на указанный email.
     */
    public static void sendReport(String recipientEmail, String attachmentPath) throws MessagingException {
        if (SENDER_EMAIL.contains("your_email") || SENDER_APP_PASSWORD.contains("your_app_password")) {
            throw new MessagingException(
                    "SMTP не настроен!\n\n" +
                            "Откройте EmailSender.java и укажите:\n" +
                            "  • SENDER_EMAIL — ваш email\n" +
                            "  • SENDER_APP_PASSWORD — пароль приложения\n\n" +
                            "Для Gmail инструкция:\n" +
                            "https://support.google.com/accounts/answer/185833"
            );
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(SENDER_EMAIL, "System Info Report", "UTF-8"));
        } catch (Exception e) {
            message.setFrom(new InternetAddress(SENDER_EMAIL));
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("🖥 Отчёт о характеристиках компьютера — " +
                java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

        MimeBodyPart textPart = new MimeBodyPart();
        String body = """
                Здравствуйте!
                
                Вы запросили отправку отчёта о характеристиках вашего компьютера.
                
                Отчёт прикреплён к этому письму в виде HTML-файла.
                Откройте его в любом браузере для просмотра.
                
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                
                Это автоматическое письмо.
                Не отвечайте на него.
                
                Дата формирования: %s
                
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                
                System Info — программа сбора характеристик ПК
                """.formatted(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        textPart.setText(body, "UTF-8");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        File file = new File(attachmentPath);
        if (!file.exists()) {
            throw new MessagingException("Файл отчёта не найден: " + attachmentPath);
        }

        attachmentPart.setDataHandler(new DataHandler(new FileDataSource(file)));
        try {
            attachmentPart.setFileName(MimeUtility.encodeText(file.getName(), "UTF-8", null));
        } catch (Exception e) {
            attachmentPart.setFileName(file.getName());
        }

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);
        message.setContent(multipart);

        Transport.send(message);
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
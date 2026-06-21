package com.systeminfo;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            UserPreferences prefs = new UserPreferences();

            if (prefs.hasAgreedToTerms()) {
                // Пользователь уже согласился — сразу к выбору категорий
                System.out.println("Пользователь уже принял гарантии (" + prefs.getAgreementDate() + ")");
                new ConsentDialog().showConsentDialog();
            } else {
                // Первый запуск — показываем гарантии
                System.out.println("Первый запуск — показываем гарантии");
                WelcomeDialog welcome = new WelcomeDialog();
                welcome.show();
            }
        });
    }
}
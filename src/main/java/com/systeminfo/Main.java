package com.systeminfo;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            UserPreferences prefs = new UserPreferences();

            // Шаг 1: Проверяем согласие с гарантиями
            if (!prefs.hasAgreedToTerms()) {
                new WelcomeDialog().show();
                return;
            }

            // Шаг 2: Проверяем, выбран ли тип устройства
            if (!prefs.hasDeviceType()) {
                new DeviceTypeDialog().show();
                return;
            }

            // Шаг 3: Всё уже настроено — сразу к выбору категорий
            new ConsentDialog().showConsentDialog();
        });
    }
}
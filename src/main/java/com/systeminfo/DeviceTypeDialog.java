package com.systeminfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Окно выбора типа устройства (ПК или ноутбук).
 */
public class DeviceTypeDialog {

    public void show() {
        JFrame frame = new JFrame("Выбор типа устройства");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(620, 480);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Главная панель с градиентом
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(245, 250, 255),
                        0, getHeight(), new Color(220, 235, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // ===== ШАПКА =====
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🖥💻", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 56));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Какое у вас устройство?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(20, 40, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel(
                "<html><div style='text-align: center;'>Выберите тип устройства для оптимального сбора данных</div></html>",
                SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        subtitleLabel.setForeground(new Color(100, 100, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ===== ПАНЕЛЬ ВЫБОРА =====
        JPanel choicePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        choicePanel.setOpaque(false);

        // ПК
        JPanel desktopCard = createDeviceCard(
                "🖥",
                "Стационарный ПК",
                "<html><div style='text-align: center;'>" +
                        "• Без аккумулятора<br>" +
                        "• Полная диагностика<br>" +
                        "• Сбор температуры CPU<br>" +
                        "• Информация о видеокарте" +
                        "</div></html>",
                new Color(33, 150, 243),
                new Color(13, 71, 161)
        );
        desktopCard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectDevice(frame, UserPreferences.DEVICE_DESKTOP);
            }
        });

        // Ноутбук
        JPanel laptopCard = createDeviceCard(
                "💻",
                "Ноутбук",
                "<html><div style='text-align: center;'>" +
                        "• Информация о батарее<br>" +
                        "• Износ аккумулятора<br>" +
                        "• Время работы<br>" +
                        "• Полная диагностика" +
                        "</div></html>",
                new Color(76, 175, 80),
                new Color(27, 94, 32)
        );
        laptopCard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectDevice(frame, UserPreferences.DEVICE_LAPTOP);
            }
        });

        choicePanel.add(desktopCard);
        choicePanel.add(laptopCard);

        mainPanel.add(choicePanel, BorderLayout.CENTER);

        // ===== ПОДСКАЗКА ВНИЗУ =====
        JPanel hintPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hintPanel.setOpaque(false);
        JLabel hintLabel = new JLabel(
                "<html><div style='text-align: center; color: #777;'>" +
                        "💡 Этот выбор будет сохранён и больше не появится<br>" +
                        "<i>Подсказка: ноутбуки имеют аккумулятор, стационарные ПК — нет</i>" +
                        "</div></html>",
                SwingConstants.CENTER);
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hintPanel.add(hintLabel);
        mainPanel.add(hintPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Создаёт карточку выбора устройства (как кнопку).
     */
    private JPanel createDeviceCard(String icon, String title, String description,
                                    Color bgColor, Color borderColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(25, 15, 25, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(iconLabel);

        card.add(Box.createVerticalStrut(10));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(borderColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(15));

        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(new Color(60, 60, 60));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(descLabel);

        // Эффект при наведении
        Color defaultBg = Color.WHITE;
        Color hoverBg = new Color(
                Math.min(255, bgColor.getRed() + 200),
                Math.min(255, bgColor.getGreen() + 200),
                Math.min(255, bgColor.getBlue() + 200)
        );

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(hoverBg);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 3),
                        BorderFactory.createEmptyBorder(24, 14, 24, 14)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(defaultBg);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 2),
                        BorderFactory.createEmptyBorder(25, 15, 25, 15)
                ));
            }
        });

        return card;
    }

    private void selectDevice(JFrame frame, String deviceType) {
        UserPreferences prefs = new UserPreferences();
        prefs.setDeviceType(deviceType);

        frame.dispose();

        // Переходим к окну согласия
        new ConsentDialog().showConsentDialog();
    }
}
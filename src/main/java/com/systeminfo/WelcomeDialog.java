package com.systeminfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Окно приветствия с гарантиями конфиденциальности.
 * Показывается при первом запуске приложения.
 * Пользователь должен прокрутить весь текст и подтвердить ознакомление,
 * прежде чем сможет нажать кнопку "Продолжить".
 */
public class WelcomeDialog {

    private JFrame frame;
    private JButton continueButton;
    private JCheckBox cbAccepted;
    private JScrollPane scrollPane;
    private boolean scrolledToBottom = false;

    /**
     * Показывает окно приветствия. После закрытия запускается основной диалог.
     */
    public void show() {
        frame = new JFrame("Добро пожаловать — System Info Collector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 720);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Главная панель с градиентом
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0)) {
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
        mainPanel.setBorder(new EmptyBorder(20, 25, 15, 25));

        // ===== ШАПКА =====
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🔒", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 56));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Гарантия конфиденциальности", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(20, 40, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Прочтите перед использованием программы",
                SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        subtitleLabel.setForeground(new Color(100, 100, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 15, 0));
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ===== ТЕКСТ ГАРАНТИЙ (со скроллом) =====
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setText(getGuaranteeHtml());
        textPane.setCaretPosition(0);
        textPane.setBackground(Color.WHITE);
        textPane.setBorder(new EmptyBorder(15, 20, 15, 20));

        scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(new LineBorder(new Color(180, 200, 220), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(640, 360));

        // Отслеживаем прокрутку до конца
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar sb = scrollPane.getVerticalScrollBar();
            int maxValue = sb.getMaximum() - sb.getVisibleAmount();
            // Если прокрутили почти до конца (с допуском 20px)
            if (sb.getValue() >= maxValue - 20) {
                scrolledToBottom = true;
                cbAccepted.setEnabled(true);
                cbAccepted.setText("✓ Я прочитал(а) и понимаю гарантии конфиденциальности");
                cbAccepted.setForeground(new Color(30, 100, 30));
            }
        });

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ===== НИЖНЯЯ ПАНЕЛЬ =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Подсказка про прокрутку
        JLabel hintLabel = new JLabel(
                "⬇ Прокрутите текст до конца, чтобы продолжить",
                SwingConstants.CENTER);
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hintLabel.setForeground(new Color(180, 80, 0));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setBorder(new EmptyBorder(5, 0, 10, 0));
        bottomPanel.add(hintLabel);

        // Чекбокс подтверждения (изначально выключен)
        cbAccepted = new JCheckBox("⏳ Сначала прокрутите текст до конца...");
        cbAccepted.setFont(new Font("SansSerif", Font.BOLD, 13));
        cbAccepted.setOpaque(false);
        cbAccepted.setEnabled(false);
        cbAccepted.setAlignmentX(Component.CENTER_ALIGNMENT);
        cbAccepted.setHorizontalAlignment(SwingConstants.CENTER);
        cbAccepted.addActionListener(e -> {
            continueButton.setEnabled(cbAccepted.isSelected());
            if (cbAccepted.isSelected()) {
                hintLabel.setText("✅ Можно продолжать!");
                hintLabel.setForeground(new Color(30, 130, 30));
            } else {
                hintLabel.setText("☐ Подтвердите ознакомление");
                hintLabel.setForeground(new Color(180, 80, 0));
            }
        });
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        checkPanel.setOpaque(false);
        checkPanel.add(cbAccepted);
        bottomPanel.add(checkPanel);

        // Кнопки
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel.setOpaque(false);

        continueButton = new JButton("▶ Продолжить");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        continueButton.setBackground(new Color(46, 125, 50));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setPreferredSize(new Dimension(200, 42));
        continueButton.setEnabled(false);
        continueButton.addActionListener(e -> {
            frame.dispose();
            // Запускаем основной диалог
            new ConsentDialog().showConsentDialog();
        });

        JButton exitButton = new JButton("✕ Выйти");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        exitButton.setBackground(new Color(120, 120, 120));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(150, 42));
        exitButton.addActionListener(e -> System.exit(0));

        buttonsPanel.add(continueButton);
        buttonsPanel.add(exitButton);
        bottomPanel.add(buttonsPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Возвращает HTML-текст с гарантиями конфиденциальности.
     */
    private String getGuaranteeHtml() {
        return """
                <html>
                <body style='font-family: Segoe UI, sans-serif; font-size: 13px; color: #2c3e50; line-height: 1.6;'>
                
                <h2 style='color: #1e5128; margin-top: 0;'>🛡 100% Конфиденциальность</h2>
                
                <p><b>System Info Collector</b> — это полностью локальное приложение, 
                которое работает только на вашем компьютере. Мы гарантируем:</p>
                
                <hr style='border: 1px solid #d0e0d0;'>
                
                <h3 style='color: #1565c0;'>🔒 1. Данные никуда не отправляются</h3>
                <ul>
                  <li>Программа <b>не имеет сетевых функций</b> для отправки данных</li>
                  <li>Нет подключений к интернету, серверам или облачным хранилищам</li>
                  <li>Все собранные данные остаются <b>исключительно на вашем компьютере</b></li>
                  <li>Вы сами решаете, куда сохранить отчёт (или не сохранять вовсе)</li>
                </ul>
                
                <h3 style='color: #1565c0;'>🛡 2. Безопасность гарантирована</h3>
                <ul>
                  <li>Программа <b>не содержит вирусов, троянов или шпионского ПО</b></li>
                  <li>Не модифицирует системные файлы и реестр</li>
                  <li>Не устанавливает фоновых служб или процессов</li>
                  <li>Не запускается автоматически при старте Windows</li>
                  <li>Использует только <b>открытые библиотеки</b> с открытым исходным кодом:
                    <ul>
                      <li><b>OSHI</b> (MIT License) — сбор системной информации</li>
                      <li><b>Gson</b> (Apache 2.0) — работа с JSON</li>
                      <li><b>SLF4J</b> (MIT License) — логирование</li>
                    </ul>
                  </li>
                </ul>
                
                <h3 style='color: #1565c0;'>👤 3. Полный контроль пользователя</h3>
                <ul>
                  <li>Без вашего <b>явного согласия</b> ничего не собирается</li>
                  <li>Вы выбираете <b>какие именно</b> категории данных собирать</li>
                  <li>Вы можете <b>отказаться</b> в любой момент</li>
                  <li>Все результаты <b>отображаются на экране</b> перед сохранением</li>
                  <li>Вы видите <b>точный путь</b> сохранения файла</li>
                </ul>
                
                <h3 style='color: #1565c0;'>📊 4. Какие данные собираются</h3>
                <p>Только <b>технические характеристики</b> вашего компьютера:</p>
                <ul>
                  <li>Модель процессора, видеокарты, материнской платы</li>
                  <li>Объём оперативной памяти и дисков</li>
                  <li>Версия операционной системы</li>
                  <li>Сетевые интерфейсы (только локальные)</li>
                  <li>Версия Java</li>
                </ul>
                
                <p style='color: #c62828;'><b>⛔ НЕ собираются:</b></p>
                <ul>
                  <li>Личные файлы, документы, фотографии</li>
                  <li>Пароли, логины, история браузера</li>
                  <li>Содержимое экрана, скриншоты</li>
                  <li>Данные с микрофона или камеры</li>
                  <li>Информация о посещённых сайтах</li>
                  <li>Платёжная информация</li>
                </ul>
                
                <h3 style='color: #1565c0;'>📜 5. Лицензия и открытый код</h3>
                <ul>
                  <li>Программа <b>распространяется бесплатно</b></li>
                  <li>Исходный код <b>доступен для проверки</b></li>
                  <li>Вы можете самостоятельно убедиться в отсутствии вредоносного кода</li>
                  <li>Использование <b>на ваш страх и риск</b> — автор не несёт ответственности
                      за неправильное использование</li>
                </ul>
                
                <h3 style='color: #1565c0;'>💡 6. Зачем нужна эта программа?</h3>
                <ul>
                  <li>📋 Создать отчёт о конфигурации для технической поддержки</li>
                  <li>🔧 Диагностика проблем с оборудованием</li>
                  <li>📊 Инвентаризация компьютерного парка</li>
                  <li>🛒 Подбор совместимых комплектующих</li>
                  <li>📝 Документирование характеристик системы</li>
                </ul>
                
                <hr style='border: 1px solid #d0e0d0;'>
                
                <div style='background: #e8f5e9; padding: 12px; border-left: 4px solid #2e7d32; margin: 15px 0;'>
                  <p style='margin: 0;'><b>✅ Итог:</b> Используя эту программу, вы получаете 
                  удобный инструмент для сбора характеристик вашего компьютера, 
                  при этом <b>ваша приватность полностью защищена</b>. 
                  Все данные остаются под вашим контролем.</p>
                </div>
                
                <p style='text-align: center; color: #888; font-size: 11px; margin-top: 20px;'>
                  <i>Прочитав этот текст до конца, вы можете подтвердить ознакомление 
                  и продолжить работу с программой.</i>
                </p>
                
                <p style='text-align: center; color: #2e7d32; font-size: 14px; font-weight: bold;'>
                  ⬇ Вы достигли конца текста ⬇
                </p>
                
                </body>
                </html>
                """;
    }
}
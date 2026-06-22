package com.systeminfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class ConsentDialog {

    private final JCheckBox cbOS = new JCheckBox("Операционная система", true);
    private final JCheckBox cbCPU = new JCheckBox("Процессор (CPU)", true);
    private final JCheckBox cbSensors = new JCheckBox("🌡 Датчики (температура CPU)", true);
    private final JCheckBox cbRAM = new JCheckBox("Оперативная память (RAM)", true);
    private final JCheckBox cbGPU = new JCheckBox("Видеокарта (GPU)", true);
    private final JCheckBox cbDisks = new JCheckBox("Диски и хранилища", true);
    private final JCheckBox cbNetwork = new JCheckBox("Сетевые интерфейсы", true);
    private final JCheckBox cbConnections = new JCheckBox("🌐 Активные сетевые соединения", true);
    private final JCheckBox cbMotherboard = new JCheckBox("Материнская плата", true);
    private final JCheckBox cbDisplay = new JCheckBox("Дисплей", true);
    private final JCheckBox cbBattery = new JCheckBox("🔋 Аккумулятор", true);
    private final JCheckBox cbSecurity = new JCheckBox("🛡 Безопасность (антивирус и брандмауэр)", true);
    private final JCheckBox cbJava = new JCheckBox("Среда Java", true);

    // ⭐ Поля для email
    private JCheckBox cbSendEmail;
    private JTextField tfEmail;

    public void showConsentDialog() {
        JFrame frame = new JFrame("Сбор информации о системе");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 920);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        UserPreferences prefs = new UserPreferences();
        boolean isLaptop = prefs.isLaptop();

        if (!isLaptop) {
            cbBattery.setSelected(false);
            cbBattery.setEnabled(false);
            cbBattery.setText("🔋 Аккумулятор (только для ноутбуков)");
            cbBattery.setForeground(Color.GRAY);
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        String deviceIcon = isLaptop ? "💻" : "🖥";
        String deviceText = isLaptop ? "ноутбук" : "стационарный ПК";

        JLabel titleLabel = new JLabel(deviceIcon + "  Сбор характеристик компьютера");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea descriptionArea = new JTextArea(
                "Данное приложение собирает технические характеристики вашего " + deviceText + ".\n\n" +
                        "📌 Отчёт сохраняется в формате HTML на ваш Рабочий стол.\n" +
                        "📌 Опционально: можно отправить на email.\n" +
                        "📌 Без вашего согласия ничего не будет собрано."
        );
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        headerPanel.add(descriptionArea, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Панель чекбоксов категорий
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "📋 Выберите категории для сбора:",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)));

        JCheckBox[] categoryBoxes = {
                cbOS, cbCPU, cbSensors, cbRAM, cbGPU, cbDisks,
                cbNetwork, cbConnections, cbMotherboard, cbDisplay,
                cbBattery, cbSecurity, cbJava
        };

        for (JCheckBox cb : categoryBoxes) {
            cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            cb.setHorizontalAlignment(SwingConstants.LEFT);
            checkboxPanel.add(cb);
            checkboxPanel.add(Box.createVerticalStrut(3));
        }

        JPanel selectButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        selectButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton selectAll = new JButton("Выбрать все");
        JButton deselectAll = new JButton("Снять все");
        selectAll.setFont(new Font("SansSerif", Font.PLAIN, 11));
        deselectAll.setFont(new Font("SansSerif", Font.PLAIN, 11));
        selectAll.addActionListener(e -> {
            for (JCheckBox cb : categoryBoxes) {
                if (cb.isEnabled()) cb.setSelected(true);
            }
        });
        deselectAll.addActionListener(e -> { for (JCheckBox cb : categoryBoxes) cb.setSelected(false); });
        selectButtonsPanel.add(selectAll);
        selectButtonsPanel.add(deselectAll);
        checkboxPanel.add(selectButtonsPanel);

        centerPanel.add(checkboxPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Панель сохранения
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "📁 Место сохранения отчёта:",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)));
        infoPanel.setBackground(new Color(245, 250, 255));

        JLabel lblInfo = new JLabel(
                "<html><div style='padding: 8px;'>" +
                        "📂 Отчёт будет сохранён на ваш <b>Рабочий стол</b><br><br>" +
                        "<span style='color: #1565c0; font-family: monospace;'>" +
                        getDesktopPath() +
                        "</span><br><br>" +
                        "Имя файла будет содержать <b>дату и время</b> создания." +
                        "</div></html>");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblInfo);

        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        // ⭐ ПАНЕЛЬ EMAIL
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "📧 Отправка на Email (опционально):",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)));
        emailPanel.setBackground(new Color(255, 250, 245));

        cbSendEmail = new JCheckBox("📧 Отправить копию отчёта на email");
        cbSendEmail.setFont(new Font("SansSerif", Font.BOLD, 13));
        cbSendEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbSendEmail.setOpaque(false);
        emailPanel.add(cbSendEmail);
        emailPanel.add(Box.createVerticalStrut(8));

        JPanel emailRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        emailRow.setOpaque(false);
        emailRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("SansSerif", Font.PLAIN, 13));
        emailRow.add(lblEmail);

        tfEmail = new JTextField(25);
        tfEmail.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tfEmail.setEnabled(false);
        emailRow.add(tfEmail);

        emailPanel.add(emailRow);

        JLabel lblEmailWarning = new JLabel(
                "<html><div style='padding: 5px; color: #c62828;'>" +
                        "⚠ <b>ВНИМАНИЕ!</b> Согласие на отправку означает, что отчёт<br>" +
                        "будет передан через интернет на указанный адрес." +
                        "</div></html>");
        lblEmailWarning.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblEmailWarning.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.add(lblEmailWarning);

        cbSendEmail.addActionListener(e -> {
            tfEmail.setEnabled(cbSendEmail.isSelected());
            if (cbSendEmail.isSelected()) {
                tfEmail.requestFocus();
            }
        });

        centerPanel.add(emailPanel);

        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // НИЖНЯЯ ПАНЕЛЬ
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton agreeButton = createStyledButton(
                "✓ Согласен — собрать данные",
                new Color(76, 175, 80),
                new Color(46, 125, 50)
        );
        agreeButton.setPreferredSize(new Dimension(300, 45));

        JButton declineButton = createStyledButton(
                "✕ Отказаться",
                new Color(244, 67, 54),
                new Color(183, 28, 28)
        );
        declineButton.setPreferredSize(new Dimension(180, 45));

        agreeButton.addActionListener(e -> {
            boolean anySelected = false;
            for (JCheckBox cb : categoryBoxes) {
                if (cb.isSelected()) { anySelected = true; break; }
            }
            if (!anySelected) {
                JOptionPane.showMessageDialog(frame,
                        "Выберите хотя бы одну категорию данных.",
                        "Ничего не выбрано", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ⭐ Проверка email
            String email = null;
            if (cbSendEmail.isSelected()) {
                email = tfEmail.getText().trim();
                if (!EmailSender.isValidEmail(email)) {
                    JOptionPane.showMessageDialog(frame,
                            "Введите корректный email адрес!\n\nПример: example@gmail.com",
                            "Неверный email", JOptionPane.WARNING_MESSAGE);
                    tfEmail.requestFocus();
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Вы уверены, что хотите отправить отчёт на email?\n\n" +
                                "📧 " + email + "\n\n" +
                                "Отчёт будет передан через интернет.",
                        "Подтверждение отправки", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            frame.dispose();
            collectAndSave(email);
        });

        declineButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,
                    "Вы отказались от сбора данных.\nПриложение будет закрыто.",
                    "Отказ", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });

        buttonPanel.add(agreeButton);
        buttonPanel.add(declineButton);

        // Дополнительные кнопки
        JPanel extraButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        extraButtonsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JButton showTermsBtn = createStyledButton(
                "🔒 Прочитать гарантии",
                new Color(33, 150, 243),
                new Color(13, 71, 161)
        );
        showTermsBtn.setPreferredSize(new Dimension(200, 36));
        showTermsBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        showTermsBtn.addActionListener(e -> {
            frame.dispose();
            new WelcomeDialog().show();
        });

        JButton changeDeviceBtn = createStyledButton(
                "🔄 Изменить тип устройства",
                new Color(255, 152, 0),
                new Color(230, 81, 0)
        );
        changeDeviceBtn.setPreferredSize(new Dimension(230, 36));
        changeDeviceBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        changeDeviceBtn.addActionListener(e -> {
            frame.dispose();
            new DeviceTypeDialog().show();
        });

        extraButtonsPanel.add(showTermsBtn);
        extraButtonsPanel.add(changeDeviceBtn);

        southPanel.add(buttonPanel);
        southPanel.add(extraButtonsPanel);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor, Color borderColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(240, 240, 240));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        Color hoverBg = bgColor.brighter();
        Color defaultBg = new Color(240, 240, 240);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(defaultBg);
            }
        });

        return button;
    }

    private String getDesktopPath() {
        return System.getProperty("user.home") + File.separator + "Desktop";
    }

    private String buildReportPath() {
        UserPreferences prefs = new UserPreferences();
        String deviceType = prefs.isLaptop() ? "laptop" : "desktop";
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        return getDesktopPath() + File.separator + "system_report_" + deviceType + "_" + timestamp + ".html";
    }

    private void collectAndSave(String email) {
        JFrame progressFrame = new JFrame("Сбор данных...");
        progressFrame.setSize(450, 130);
        progressFrame.setLocationRelativeTo(null);
        progressFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        progressFrame.setResizable(false);

        JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
        progressPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel statusLabel = new JLabel("⏳ Собираем информацию о системе...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressPanel.add(statusLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressFrame.setContentPane(progressPanel);
        progressFrame.setVisible(true);

        SwingWorker<Map<String, Map<String, String>>, String> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Map<String, String>> doInBackground() {
                publish("⏳ Собираем информацию о системе...");
                SystemInfoCollector collector = new SystemInfoCollector();
                return collector.collect(
                        cbOS.isSelected(), cbCPU.isSelected(), cbRAM.isSelected(),
                        cbGPU.isSelected(), cbDisks.isSelected(), cbNetwork.isSelected(),
                        cbMotherboard.isSelected(), cbDisplay.isSelected(), cbJava.isSelected(),
                        cbBattery.isSelected(), cbSensors.isSelected(),
                        cbConnections.isSelected(), cbSecurity.isSelected()
                );
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                if (!chunks.isEmpty()) {
                    statusLabel.setText(chunks.get(chunks.size() - 1));
                }
            }

            @Override
            protected void done() {
                try {
                    Map<String, Map<String, String>> data = get();

                    statusLabel.setText("💾 Сохраняем отчёт...");

                    String savedPath = buildReportPath();

                    File desktopDir = new File(getDesktopPath());
                    if (!desktopDir.exists()) {
                        desktopDir.mkdirs();
                    }

                    ReportGenerator.saveToHtml(data, savedPath);

                    boolean emailSent = false;
                    String emailError = null;
                    if (email != null && !email.isBlank()) {
                        try {
                            statusLabel.setText("📧 Отправляем email...");
                            EmailSender.sendReport(email, savedPath);
                            emailSent = true;
                        } catch (Exception emailEx) {
                            emailError = emailEx.getMessage();
                            emailEx.printStackTrace();
                        }
                    }

                    progressFrame.dispose();
                    showSuccessDialog(savedPath, email, emailSent, emailError);

                } catch (Exception ex) {
                    progressFrame.dispose();
                    JOptionPane.showMessageDialog(null,
                            "Ошибка при сборе/сохранении данных:\n" + ex.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        };
        worker.execute();
    }

    private void showSuccessDialog(String savedPath, String email, boolean emailSent, String emailError) {
        JFrame frame = new JFrame("Успех");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(560, 440);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(232, 245, 233),
                        0, getHeight(), new Color(200, 230, 201));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("✅", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 72));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("УСПЕХ!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(27, 94, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        centerPanel.add(titleLabel);

        StringBuilder msg = new StringBuilder("<html><div style='text-align: center; padding: 10px;'>");
        msg.append("Файл сохранён на<br><b>вашем Рабочем столе</b>");

        if (email != null) {
            msg.append("<br><br>");
            if (emailSent) {
                msg.append("<span style='color: #1565c0; font-size: 14px;'>")
                        .append("📧 Отправлено на:<br><b>")
                        .append(email).append("</b></span>");
            } else {
                msg.append("<span style='color: #c62828; font-size: 12px;'>")
                        .append("⚠ Не удалось отправить на email:<br>")
                        .append(emailError != null ? emailError.replace("\n", "<br>") : "Неизвестная ошибка")
                        .append("</span>");
            }
        }
        msg.append("</div></html>");

        JLabel messageLabel = new JLabel(msg.toString(), SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        messageLabel.setForeground(new Color(46, 80, 50));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(messageLabel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonPanel.setOpaque(false);

        JButton closeBtn = createStyledButton(
                "Закрыть",
                new Color(158, 158, 158),
                new Color(97, 97, 97)
        );
        closeBtn.setPreferredSize(new Dimension(160, 42));
        closeBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
}
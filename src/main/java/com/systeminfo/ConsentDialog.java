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
    private final JCheckBox cbRAM = new JCheckBox("Оперативная память (RAM)", true);
    private final JCheckBox cbGPU = new JCheckBox("Видеокарта (GPU)", true);
    private final JCheckBox cbDisks = new JCheckBox("Диски и хранилища", true);
    private final JCheckBox cbNetwork = new JCheckBox("Сетевые интерфейсы", true);
    private final JCheckBox cbMotherboard = new JCheckBox("Материнская плата", true);
    private final JCheckBox cbDisplay = new JCheckBox("Дисплей", true);
    private final JCheckBox cbJava = new JCheckBox("Среда Java", true);

    public void showConsentDialog() {
        JFrame frame = new JFrame("Сбор информации о системе");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(680, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ===== Верхняя панель =====
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("🖥  Сбор характеристик компьютера");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea descriptionArea = new JTextArea(
                "Данное приложение собирает технические характеристики вашего компьютера.\n\n" +
                        "📌 Отчёт сохраняется в формате HTML на ваш Рабочий стол.\n" +
                        "📌 Данные сохраняются ТОЛЬКО локально на вашем компьютере.\n" +
                        "📌 Без вашего согласия ничего не будет собрано."
        );
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        headerPanel.add(descriptionArea, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ===== Центральная панель =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "📋 Выберите категории для сбора:",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)));

        JCheckBox[] categoryBoxes = {cbOS, cbCPU, cbRAM, cbGPU, cbDisks, cbNetwork, cbMotherboard, cbDisplay, cbJava};
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
        selectAll.addActionListener(e -> { for (JCheckBox cb : categoryBoxes) cb.setSelected(true); });
        deselectAll.addActionListener(e -> { for (JCheckBox cb : categoryBoxes) cb.setSelected(false); });
        selectButtonsPanel.add(selectAll);
        selectButtonsPanel.add(deselectAll);
        checkboxPanel.add(selectButtonsPanel);

        centerPanel.add(checkboxPanel);
        centerPanel.add(Box.createVerticalStrut(10));

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

        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // ===== НИЖНЯЯ ПАНЕЛЬ С КНОПКАМИ =====
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        // Основные кнопки (Согласен / Отказаться)
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

            frame.dispose();
            collectAndSave();
        });

        declineButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,
                    "Вы отказались от сбора данных.\nПриложение будет закрыто.",
                    "Отказ", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });

        buttonPanel.add(agreeButton);
        buttonPanel.add(declineButton);

        // ⭐ Панель с кнопкой "Прочитать гарантии заново"
        JPanel termsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        termsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JButton showTermsBtn = createStyledButton(
                "🔒 Прочитать гарантии конфиденциальности",
                new Color(33, 150, 243),
                new Color(13, 71, 161)
        );
        showTermsBtn.setPreferredSize(new Dimension(360, 38));
        showTermsBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        showTermsBtn.addActionListener(e -> {
            frame.dispose();
            new WelcomeDialog().show();
        });

        termsPanel.add(showTermsBtn);

        // Добавляем обе панели снизу
        southPanel.add(buttonPanel);
        southPanel.add(termsPanel);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Создаёт стилизованную кнопку с ЧЁРНЫМ текстом и цветной рамкой.
     */
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
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        return getDesktopPath() + File.separator + "system_report_" + timestamp + ".html";
    }

    private void collectAndSave() {
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

        SwingWorker<Map<String, Map<String, String>>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Map<String, String>> doInBackground() {
                SystemInfoCollector collector = new SystemInfoCollector();
                return collector.collect(
                        cbOS.isSelected(), cbCPU.isSelected(), cbRAM.isSelected(),
                        cbGPU.isSelected(), cbDisks.isSelected(), cbNetwork.isSelected(),
                        cbMotherboard.isSelected(), cbDisplay.isSelected(), cbJava.isSelected()
                );
            }

            @Override
            protected void done() {
                progressFrame.dispose();
                try {
                    Map<String, Map<String, String>> data = get();

                    String savedPath = buildReportPath();

                    File desktopDir = new File(getDesktopPath());
                    if (!desktopDir.exists()) {
                        desktopDir.mkdirs();
                    }

                    ReportGenerator.saveToHtml(data, savedPath);
                    showSuccessDialog(savedPath);

                } catch (Exception ex) {
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

    private void showSuccessDialog(String savedPath) {
        JFrame frame = new JFrame("Успех");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 360);
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

        JLabel messageLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "Файл находится на<br><b>вашем Рабочем столе</b>" +
                        "</div></html>",
                SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
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
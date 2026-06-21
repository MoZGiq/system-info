package com.systeminfo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Генерирует красивый HTML-отчёт.
 */
public class ReportGenerator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Генерирует текстовое представление для показа в окне результатов.
     * (Простой текст, не HTML — для отображения в JTextArea)
     */
    public static String generateTextReport(Map<String, Map<String, String>> data) {
        StringBuilder sb = new StringBuilder();
        String date = LocalDateTime.now().format(FMT);

        sb.append("\n");
        sb.append("  ╔═══════════════════════════════════════════════════════════════════╗\n");
        sb.append("  ║          🖥  ОТЧЁТ О ХАРАКТЕРИСТИКАХ КОМПЬЮТЕРА  🖥               ║\n");
        sb.append("  ║          Дата: ").append(String.format("%-50s", date)).append("  ║\n");
        sb.append("  ║          System Info v2.0                               ║\n");
        sb.append("  ╚═══════════════════════════════════════════════════════════════════╝\n\n");

        int sectionNum = 1;
        for (Map.Entry<String, Map<String, String>> category : data.entrySet()) {
            String title = sectionNum + ". " + category.getKey().toUpperCase();

            sb.append("  ┌─────────────────────────────────────────────────────────────────┐\n");
            sb.append("  │  ").append(String.format("%-64s", title)).append("│\n");
            sb.append("  ├─────────────────────────────────────────────────────────────────┤\n");

            for (Map.Entry<String, String> entry : category.getValue().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.length() + value.length() > 60) {
                    sb.append("  │  ").append(String.format("%-64s", key + ":")).append("│\n");
                    int maxLen = 60;
                    int pos = 0;
                    while (pos < value.length()) {
                        int end = Math.min(pos + maxLen, value.length());
                        String chunk = value.substring(pos, end);
                        sb.append("  │    ").append(String.format("%-62s", chunk)).append("│\n");
                        pos = end;
                    }
                } else {
                    String line = String.format("%-32s  %s", key + ":", value);
                    sb.append("  │  ").append(String.format("%-64s", line)).append("│\n");
                }
            }

            sb.append("  └─────────────────────────────────────────────────────────────────┘\n\n");
            sectionNum++;
        }

        sb.append("  ═══════════════════════════════════════════════════════════════════\n");
        sb.append("  Конец отчёта. Всего секций: ").append(data.size()).append("\n");
        sb.append("  Сформировано ").append(date).append("\n");
        sb.append("  ═══════════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    /**
     * Сохраняет отчёт как красивый HTML-файл.
     */
    public static void saveToHtml(Map<String, Map<String, String>> data, String filePath) throws IOException {
        StringBuilder html = new StringBuilder();
        String date = LocalDateTime.now().format(FMT);

        html.append("""
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Отчёт о системе</title>
                  <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    
                    body {
                      font-family: 'Segoe UI', 'Inter', Tahoma, sans-serif;
                      background: linear-gradient(135deg, #0c0c1d 0%, #1a1a3e 50%, #0c0c1d 100%);
                      color: #e0e0e0;
                      min-height: 100vh;
                      padding: 30px;
                    }
                    
                    .container { max-width: 900px; margin: 0 auto; }
                    
                    .header {
                      text-align: center;
                      padding: 40px 20px;
                      background: linear-gradient(135deg, #1e3a5f 0%, #2d1b69 100%);
                      border-radius: 20px;
                      margin-bottom: 30px;
                      box-shadow: 0 10px 40px rgba(0,0,0,0.4);
                      position: relative;
                      overflow: hidden;
                    }
                    .header::before {
                      content: '';
                      position: absolute;
                      top: -50%; left: -50%;
                      width: 200%; height: 200%;
                      background: radial-gradient(circle, rgba(255,255,255,0.03) 0%, transparent 70%);
                      animation: rotate 20s linear infinite;
                    }
                    @keyframes rotate { to { transform: rotate(360deg); } }
                    
                    .header h1 {
                      font-size: 28px;
                      font-weight: 700;
                      color: #fff;
                      position: relative;
                      z-index: 1;
                      text-shadow: 0 2px 10px rgba(0,0,0,0.3);
                    }
                    .header .subtitle {
                      color: #a0b4cc;
                      font-size: 14px;
                      margin-top: 8px;
                      position: relative;
                      z-index: 1;
                    }
                    .header .date {
                      color: #7ecfff;
                      font-size: 13px;
                      margin-top: 12px;
                      position: relative;
                      z-index: 1;
                    }
                    
                    .section {
                      background: rgba(255,255,255,0.04);
                      backdrop-filter: blur(10px);
                      border: 1px solid rgba(255,255,255,0.08);
                      border-radius: 16px;
                      margin-bottom: 20px;
                      overflow: hidden;
                      box-shadow: 0 4px 20px rgba(0,0,0,0.2);
                      transition: transform 0.2s, box-shadow 0.2s;
                    }
                    .section:hover {
                      transform: translateY(-2px);
                      box-shadow: 0 8px 30px rgba(0,0,0,0.3);
                    }
                    
                    .section-header {
                      background: linear-gradient(90deg, rgba(99,102,241,0.2) 0%, rgba(139,92,246,0.1) 100%);
                      padding: 16px 24px;
                      border-bottom: 1px solid rgba(255,255,255,0.06);
                    }
                    .section-header h2 {
                      font-size: 16px;
                      font-weight: 600;
                      color: #a78bfa;
                      letter-spacing: 0.5px;
                    }
                    
                    table { width: 100%; border-collapse: collapse; }
                    
                    tr { transition: background 0.15s; }
                    tr:hover { background: rgba(255,255,255,0.03); }
                    
                    td {
                      padding: 12px 24px;
                      border-bottom: 1px solid rgba(255,255,255,0.04);
                      font-size: 13px;
                      line-height: 1.5;
                    }
                    td:first-child {
                      color: #94a3b8;
                      width: 40%;
                      font-weight: 500;
                    }
                    td:last-child {
                      color: #e2e8f0;
                      word-break: break-word;
                      font-family: 'Consolas', 'Courier New', monospace;
                    }
                    
                    .footer {
                      text-align: center;
                      color: #4a5568;
                      margin-top: 40px;
                      padding: 20px;
                      font-size: 12px;
                      border-top: 1px solid rgba(255,255,255,0.05);
                    }
                    
                    @media print {
                      body { background: white; color: #333; padding: 10px; }
                      .section { border: 1px solid #ddd; box-shadow: none; }
                      .header { background: #f0f0f0; }
                      .header h1 { color: #333; }
                      td:first-child { color: #666; }
                      td:last-child { color: #333; }
                    }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header">
                      <h1>🖥 Отчёт о характеристиках компьютера</h1>
                      <p class="subtitle">System Info v1.0 • Данные собраны с согласия пользователя</p>
                """);

        html.append("      <p class=\"date\">📅 ").append(date).append("</p>\n");
        html.append("    </div>\n\n");

        int sectionNum = 1;
        for (Map.Entry<String, Map<String, String>> category : data.entrySet()) {
            html.append("    <div class=\"section\">\n");
            html.append("      <div class=\"section-header\">\n");
            html.append("        <h2>").append(sectionNum).append(". ")
                    .append(escapeHtml(category.getKey())).append("</h2>\n");
            html.append("      </div>\n");
            html.append("      <table>\n");

            for (Map.Entry<String, String> entry : category.getValue().entrySet()) {
                html.append("        <tr><td>").append(escapeHtml(entry.getKey()))
                        .append("</td><td>").append(escapeHtml(entry.getValue()))
                        .append("</td></tr>\n");
            }

            html.append("      </table>\n");
            html.append("    </div>\n\n");
            sectionNum++;
        }

        html.append("    <div class=\"footer\">\n");
        html.append("      <p>System Info v1.0 • Отчёт сформирован ").append(date).append("</p>\n");
        html.append("      <p>Всего секций: ").append(data.size()).append(" • Данные собраны с согласия пользователя</p>\n");
        html.append("    </div>\n");
        html.append("  </div>\n</body>\n</html>");

        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(html.toString());
        }
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
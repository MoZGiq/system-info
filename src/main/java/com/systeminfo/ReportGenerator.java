package com.systeminfo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ReportGenerator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static String generateTextReport(Map<String, Map<String, String>> data) {
        StringBuilder sb = new StringBuilder();
        String date = LocalDateTime.now().format(FMT);

        sb.append("\n");
        sb.append("  ╔═══════════════════════════════════════════════════════════════════╗\n");
        sb.append("  ║          🖥  ОТЧЁТ О ХАРАКТЕРИСТИКАХ КОМПЬЮТЕРА  🖥               ║\n");
        sb.append("  ║          Дата: ").append(String.format("%-50s", date)).append("  ║\n");
        sb.append("  ║          System Info v1.0                                         ║\n");
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
                    
                    html {
                      min-height: 100%;
                      background: #0c0c1d;
                    }
                    
                    html, body {
                      font-family: 'Segoe UI', 'Inter', Tahoma, sans-serif;
                      color: #e0e0e0;
                    }
                    
                    /* ⭐ ОПТИМИЗИРОВАННЫЙ ФОН */
                    body {
                      min-height: 100vh;
                      background: linear-gradient(-45deg, #0c0c1d, #1a1a3e, #2d1b69, #1e3a5f, #0c0c1d);
                      background-size: 400% 400%;
                      background-attachment: fixed;
                      animation: gradientShift 30s ease infinite;
                      padding: 30px;
                      will-change: background-position;
                    }
                    
                    @keyframes gradientShift {
                      0% { background-position: 0% 50%; }
                      50% { background-position: 100% 50%; }
                      100% { background-position: 0% 50%; }
                    }
                    
                    /* ⭐ СТАТИЧНЫЕ ЗВЁЗДЫ (без анимации движения) */
                    body::before {
                      content: '';
                      position: fixed;
                      top: 0; left: 0;
                      width: 100%; height: 100%;
                      background-image: 
                        radial-gradient(2px 2px at 20px 30px, rgba(255,255,255,0.6), transparent),
                        radial-gradient(2px 2px at 60px 70px, rgba(255,255,255,0.4), transparent),
                        radial-gradient(1px 1px at 50px 50px, rgba(255,255,255,0.7), transparent),
                        radial-gradient(1px 1px at 130px 80px, rgba(255,255,255,0.5), transparent),
                        radial-gradient(2px 2px at 90px 10px, rgba(255,255,255,0.6), transparent),
                        radial-gradient(1px 1px at 160px 120px, rgba(255,255,255,0.4), transparent),
                        radial-gradient(2px 2px at 200px 200px, rgba(255,255,255,0.5), transparent),
                        radial-gradient(1px 1px at 300px 100px, rgba(255,255,255,0.6), transparent);
                      background-repeat: repeat;
                      background-size: 300px 300px;
                      pointer-events: none;
                      z-index: 0;
                    }
                    
                    .container { 
                      max-width: 900px; 
                      margin: 0 auto; 
                      position: relative;
                      z-index: 1;
                    }
                    
                    /* ⭐ ОБЛЕГЧЁННЫЙ ЗАГОЛОВОК */
                    .header {
                      text-align: center;
                      padding: 40px 20px;
                      background: linear-gradient(135deg, rgba(30, 58, 95, 0.9) 0%, rgba(45, 27, 105, 0.9) 100%);
                      border-radius: 20px;
                      margin-bottom: 30px;
                      box-shadow: 0 10px 40px rgba(0,0,0,0.4);
                      border: 1px solid rgba(139, 92, 246, 0.3);
                    }
                    
                    .header h1 {
                      font-size: 32px;
                      font-weight: 700;
                      color: #fff;
                      text-shadow: 0 2px 10px rgba(0,0,0,0.3), 0 0 30px rgba(139, 92, 246, 0.5);
                    }
                    
                    .header .subtitle {
                      color: #a0b4cc;
                      font-size: 14px;
                      margin-top: 8px;
                    }
                    
                    .header .date {
                      color: #7ecfff;
                      font-size: 13px;
                      margin-top: 12px;
                    }
                    
                    /* ⭐ ОБЛЕГЧЁННЫЕ СЕКЦИИ (без backdrop-filter) */
                    .section {
                      background: rgba(20, 20, 45, 0.85);
                      border: 1px solid rgba(139, 92, 246, 0.2);
                      border-radius: 16px;
                      margin-bottom: 20px;
                      overflow: hidden;
                      box-shadow: 0 4px 20px rgba(0,0,0,0.3);
                      transition: transform 0.2s ease, border-color 0.2s ease;
                    }
                    
                    .section:hover {
                      transform: translateY(-2px);
                      border-color: rgba(139, 92, 246, 0.5);
                    }
                    
                    .section-header {
                      background: linear-gradient(90deg, 
                        rgba(99,102,241,0.3) 0%, 
                        rgba(139,92,246,0.15) 100%);
                      padding: 16px 24px;
                      border-bottom: 1px solid rgba(255,255,255,0.06);
                    }
                    
                    .section-header h2 {
                      font-size: 16px;
                      font-weight: 600;
                      color: #c4b5fd;
                      letter-spacing: 0.5px;
                    }
                    
                    table { width: 100%; border-collapse: collapse; }
                    
                    tr { transition: background 0.15s ease; }
                    
                    tr:hover { 
                      background: rgba(139, 92, 246, 0.1);
                    }
                    
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
                    
                    /* ⭐ ФУТЕР */
                    .footer {
                      text-align: center;
                      color: #6b7280;
                      margin-top: 40px;
                      padding: 25px;
                      font-size: 12px;
                      border-top: 1px solid rgba(255,255,255,0.05);
                      background: rgba(0,0,0,0.3);
                      border-radius: 16px;
                      position: relative;
                      z-index: 1;
                    }
                    
                    .footer p {
                      margin: 5px 0;
                    }
                    
                    .footer-heart {
                      display: inline-block;
                      color: #ef4444;
                      animation: heartbeat 1.5s ease-in-out infinite;
                    }
                    
                    @keyframes heartbeat {
                      0%, 100% { transform: scale(1); }
                      50% { transform: scale(1.2); }
                    }
                    
                    /* ⭐ СКРОЛЛБАР */
                    ::-webkit-scrollbar {
                      width: 12px;
                    }
                    
                    ::-webkit-scrollbar-track {
                      background: rgba(0,0,0,0.3);
                    }
                    
                    ::-webkit-scrollbar-thumb {
                      background: linear-gradient(180deg, #6366f1, #8b5cf6);
                      border-radius: 6px;
                    }
                    
                    ::-webkit-scrollbar-thumb:hover {
                      background: linear-gradient(180deg, #818cf8, #a78bfa);
                    }
                    
                    /* ⭐ ПЕЧАТЬ */
                    @media print {
                      html, body { 
                        background: white !important; 
                        color: #333 !important; 
                        padding: 10px; 
                        animation: none !important;
                      }
                      body::before { display: none; }
                      .section { 
                        border: 1px solid #ddd; 
                        box-shadow: none; 
                        background: white !important;
                      }
                      .header { 
                        background: #f0f0f0 !important; 
                        animation: none !important;
                      }
                      .header h1 { color: #333 !important; }
                      .section-header h2 { color: #4c1d95 !important; }
                      td:first-child { color: #666 !important; }
                      td:last-child { color: #333 !important; }
                    }
                    
                    /* ⭐ МОБИЛЬНАЯ ВЕРСИЯ */
                    @media (max-width: 768px) {
                      body { padding: 15px; }
                      .header h1 { font-size: 22px; }
                      td { padding: 10px 15px; font-size: 12px; }
                    }
                    
                    /* ⭐ ОТКЛЮЧЕНИЕ АНИМАЦИЙ ДЛЯ СЛАБЫХ ПК */
                    @media (prefers-reduced-motion: reduce) {
                      *, *::before, *::after {
                        animation-duration: 0.01ms !important;
                        animation-iteration-count: 1 !important;
                        transition-duration: 0.01ms !important;
                      }
                    }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header">
                      <h1>🖥 Отчёт о характеристиках компьютера</h1>
                      <p class="subtitle">System Info • Данные собраны с согласия пользователя</p>
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
        html.append("      <p>🖥 <b>System Info</b> • Отчёт сформирован ").append(date).append("</p>\n");
        html.append("      <p>Всего секций: ").append(data.size()).append(" • Данные собраны с согласия пользователя</p>\n");
        html.append("      <p>Сделано с <span class=\"footer-heart\">❤</span> для удобной диагностики ПК</p>\n");
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
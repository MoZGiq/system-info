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
                    
                    /* ⭐ ИСПРАВЛЕНИЕ ФОНА ПРИ СКРОЛЛЕ */
                    html {
                      min-height: 100%;
                      background: #0c0c1d;
                    }
                    
                    html, body {
                      font-family: 'Segoe UI', 'Inter', Tahoma, sans-serif;
                      color: #e0e0e0;
                      overflow-x: hidden;
                    }
                    
                    /* ⭐ АНИМИРОВАННЫЙ ФИКСИРОВАННЫЙ ФОН */
                    body {
                      min-height: 100vh;
                      background: linear-gradient(-45deg, #0c0c1d, #1a1a3e, #2d1b69, #1e3a5f, #0c0c1d);
                      background-size: 400% 400%;
                      background-attachment: fixed;
                      animation: gradientShift 15s ease infinite;
                      position: relative;
                      padding: 30px;
                    }
                    
                    @keyframes gradientShift {
                      0% { background-position: 0% 50%; }
                      50% { background-position: 100% 50%; }
                      100% { background-position: 0% 50%; }
                    }
                    
                    /* ⭐ ЗВЁЗДЫ — ФИКСИРОВАННЫЕ */
                    body::before {
                      content: '';
                      position: fixed;
                      top: 0; left: 0;
                      width: 100%; height: 100%;
                      background-image: 
                        radial-gradient(2px 2px at 20px 30px, rgba(255,255,255,0.5), transparent),
                        radial-gradient(2px 2px at 60px 70px, rgba(255,255,255,0.3), transparent),
                        radial-gradient(1px 1px at 50px 50px, rgba(255,255,255,0.6), transparent),
                        radial-gradient(1px 1px at 130px 80px, rgba(255,255,255,0.4), transparent),
                        radial-gradient(2px 2px at 90px 10px, rgba(255,255,255,0.5), transparent),
                        radial-gradient(1px 1px at 160px 120px, rgba(255,255,255,0.3), transparent);
                      background-repeat: repeat;
                      background-size: 200px 200px;
                      animation: starsMove 50s linear infinite;
                      pointer-events: none;
                      z-index: 0;
                    }
                    
                    @keyframes starsMove {
                      from { background-position: 0 0; }
                      to { background-position: 200px 200px; }
                    }
                    
                    /* ⭐ ПЛАВАЮЩИЕ ЧАСТИЦЫ — ФИКСИРОВАННЫЕ */
                    body::after {
                      content: '';
                      position: fixed;
                      top: 0; left: 0;
                      width: 100%; height: 100%;
                      background-image:
                        radial-gradient(circle at 20% 50%, rgba(120, 100, 255, 0.1) 0%, transparent 50%),
                        radial-gradient(circle at 80% 80%, rgba(255, 100, 200, 0.08) 0%, transparent 50%),
                        radial-gradient(circle at 40% 20%, rgba(100, 200, 255, 0.1) 0%, transparent 50%);
                      animation: floatBubbles 20s ease-in-out infinite;
                      pointer-events: none;
                      z-index: 0;
                    }
                    
                    @keyframes floatBubbles {
                      0%, 100% { transform: translate(0, 0) scale(1); }
                      33% { transform: translate(30px, -30px) scale(1.1); }
                      66% { transform: translate(-20px, 20px) scale(0.95); }
                    }
                    
                    .container { 
                      max-width: 900px; 
                      margin: 0 auto; 
                      position: relative;
                      z-index: 1;
                    }
                    
                    .header {
                      text-align: center;
                      padding: 40px 20px;
                      background: linear-gradient(135deg, rgba(30, 58, 95, 0.85) 0%, rgba(45, 27, 105, 0.85) 100%);
                      border-radius: 20px;
                      margin-bottom: 30px;
                      box-shadow: 
                        0 10px 40px rgba(0,0,0,0.4),
                        0 0 80px rgba(139, 92, 246, 0.15);
                      position: relative;
                      overflow: hidden;
                      backdrop-filter: blur(10px);
                      border: 1px solid rgba(139, 92, 246, 0.2);
                    }
                    
                    .header::before {
                      content: '';
                      position: absolute;
                      top: -50%; left: -50%;
                      width: 200%; height: 200%;
                      background: radial-gradient(circle, rgba(255,255,255,0.05) 0%, transparent 70%);
                      animation: rotate 20s linear infinite;
                    }
                    
                    .header::after {
                      content: '';
                      position: absolute;
                      top: 0; left: -100%;
                      width: 100%; height: 100%;
                      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
                      animation: shine 4s ease-in-out infinite;
                    }
                    
                    @keyframes rotate { 
                      to { transform: rotate(360deg); } 
                    }
                    
                    @keyframes shine {
                      0% { left: -100%; }
                      50%, 100% { left: 100%; }
                    }
                    
                    .header h1 {
                      font-size: 32px;
                      font-weight: 700;
                      color: #fff;
                      position: relative;
                      z-index: 1;
                      text-shadow: 
                        0 2px 10px rgba(0,0,0,0.3),
                        0 0 30px rgba(139, 92, 246, 0.5);
                      animation: titlePulse 3s ease-in-out infinite;
                    }
                    
                    @keyframes titlePulse {
                      0%, 100% { text-shadow: 0 2px 10px rgba(0,0,0,0.3), 0 0 30px rgba(139, 92, 246, 0.5); }
                      50% { text-shadow: 0 2px 10px rgba(0,0,0,0.3), 0 0 50px rgba(139, 92, 246, 0.9); }
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
                      background: rgba(255,255,255,0.06);
                      backdrop-filter: blur(15px);
                      border: 1px solid rgba(139, 92, 246, 0.15);
                      border-radius: 16px;
                      margin-bottom: 20px;
                      overflow: hidden;
                      box-shadow: 
                        0 4px 20px rgba(0,0,0,0.3),
                        0 0 40px rgba(139, 92, 246, 0.05);
                      transition: all 0.3s ease;
                      animation: fadeInUp 0.6s ease-out backwards;
                    }
                    
                    .section:nth-child(1) { animation-delay: 0.1s; }
                    .section:nth-child(2) { animation-delay: 0.2s; }
                    .section:nth-child(3) { animation-delay: 0.3s; }
                    .section:nth-child(4) { animation-delay: 0.4s; }
                    .section:nth-child(5) { animation-delay: 0.5s; }
                    .section:nth-child(6) { animation-delay: 0.6s; }
                    .section:nth-child(7) { animation-delay: 0.7s; }
                    .section:nth-child(8) { animation-delay: 0.8s; }
                    .section:nth-child(9) { animation-delay: 0.9s; }
                    
                    @keyframes fadeInUp {
                      from {
                        opacity: 0;
                        transform: translateY(20px);
                      }
                      to {
                        opacity: 1;
                        transform: translateY(0);
                      }
                    }
                    
                    .section:hover {
                      transform: translateY(-3px);
                      box-shadow: 
                        0 10px 40px rgba(0,0,0,0.4),
                        0 0 60px rgba(139, 92, 246, 0.2);
                      border-color: rgba(139, 92, 246, 0.4);
                    }
                    
                    .section-header {
                      background: linear-gradient(90deg, 
                        rgba(99,102,241,0.25) 0%, 
                        rgba(139,92,246,0.15) 50%,
                        rgba(99,102,241,0.05) 100%);
                      padding: 16px 24px;
                      border-bottom: 1px solid rgba(255,255,255,0.06);
                      position: relative;
                      overflow: hidden;
                    }
                    
                    .section-header::before {
                      content: '';
                      position: absolute;
                      top: 0; left: -100%;
                      width: 100%; height: 100%;
                      background: linear-gradient(90deg, transparent, rgba(139,92,246,0.1), transparent);
                      transition: left 0.6s ease;
                    }
                    
                    .section:hover .section-header::before {
                      left: 100%;
                    }
                    
                    .section-header h2 {
                      font-size: 16px;
                      font-weight: 600;
                      color: #c4b5fd;
                      letter-spacing: 0.5px;
                      position: relative;
                      z-index: 1;
                      text-shadow: 0 0 20px rgba(196, 181, 253, 0.3);
                    }
                    
                    table { width: 100%; border-collapse: collapse; }
                    
                    tr { transition: all 0.2s ease; }
                    
                    tr:hover { 
                      background: rgba(139, 92, 246, 0.08);
                      transform: translateX(3px);
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
                    
                    .footer {
                      text-align: center;
                      color: #6b7280;
                      margin-top: 40px;
                      padding: 25px;
                      font-size: 12px;
                      border-top: 1px solid rgba(255,255,255,0.05);
                      background: rgba(0,0,0,0.2);
                      border-radius: 16px;
                      backdrop-filter: blur(10px);
                      position: relative;
                      z-index: 1;
                    }
                    
                    .footer p {
                      margin: 5px 0;
                    }
                    
                    .footer-heart {
                      display: inline-block;
                      animation: heartbeat 1.5s ease-in-out infinite;
                      color: #ef4444;
                    }
                    
                    @keyframes heartbeat {
                      0%, 100% { transform: scale(1); }
                      50% { transform: scale(1.2); }
                    }
                    
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
                    
                    @media print {
                      html, body { 
                        background: white !important; 
                        color: #333 !important; 
                        padding: 10px; 
                        animation: none !important;
                      }
                      body::before, body::after { display: none; }
                      .section { 
                        border: 1px solid #ddd; 
                        box-shadow: none; 
                        background: white !important;
                        animation: none !important;
                      }
                      .header { 
                        background: #f0f0f0 !important; 
                        animation: none !important;
                      }
                      .header h1 { color: #333 !important; animation: none !important; }
                      .section-header h2 { color: #4c1d95 !important; }
                      td:first-child { color: #666 !important; }
                      td:last-child { color: #333 !important; }
                    }
                    
                    @media (max-width: 768px) {
                      body { padding: 15px; }
                      .header h1 { font-size: 22px; }
                      td { padding: 10px 15px; font-size: 12px; }
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
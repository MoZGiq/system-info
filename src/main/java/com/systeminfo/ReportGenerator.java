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
                String line = String.format("%-32s  %s", entry.getKey() + ":", entry.getValue());
                sb.append("  │  ").append(String.format("%-64s", line)).append("│\n");
            }
            sb.append("  └─────────────────────────────────────────────────────────────────┘\n\n");
            sectionNum++;
        }

        return sb.toString();
    }

    /**
     * Генерирует краткую сводку (понятным языком).
     */
    private static String generateSummary(Map<String, Map<String, String>> data) {
        StringBuilder summary = new StringBuilder();

        // ОС
        Map<String, String> os = data.get("Операционная система");
        if (os != null) {
            String osName = os.getOrDefault("Название ОС", "Неизвестно");
            String hostname = os.getOrDefault("Имя компьютера", "Неизвестно");
            summary.append("<div class='summary-item'>");
            summary.append("<div class='summary-icon'>💻</div>");
            summary.append("<div class='summary-text'>");
            summary.append("<div class='summary-label'>Компьютер</div>");
            summary.append("<div class='summary-value'>").append(escapeHtml(hostname)).append("</div>");
            summary.append("<div class='summary-sub'>").append(escapeHtml(osName)).append("</div>");
            summary.append("</div></div>");
        }

        // CPU
        Map<String, String> cpu = data.get("Процессор (CPU)");
        if (cpu != null) {
            String cpuName = cpu.getOrDefault("Название", "Неизвестно");
            String cores = cpu.getOrDefault("Физические ядра", "?");
            String threads = cpu.getOrDefault("Логические ядра", "?");
            String freq = cpu.getOrDefault("Макс. частота", "?");
            String load = cpu.getOrDefault("Текущая загрузка", "?");
            summary.append("<div class='summary-item'>");
            summary.append("<div class='summary-icon'>🔲</div>");
            summary.append("<div class='summary-text'>");
            summary.append("<div class='summary-label'>Процессор</div>");
            summary.append("<div class='summary-value'>").append(escapeHtml(cpuName)).append("</div>");
            summary.append("<div class='summary-sub'>").append(cores).append(" ядер / ")
                    .append(threads).append(" потоков • ")
                    .append(escapeHtml(freq)).append(" • Загрузка: ")
                    .append(escapeHtml(load)).append("</div>");
            summary.append("</div></div>");
        }

        // Температура CPU
        Map<String, String> sensors = data.get("Датчики (температура)");
        if (sensors != null && sensors.containsKey("Температура CPU")) {
            String temp = sensors.get("Температура CPU");
            String status = sensors.getOrDefault("Статус температуры", "");
            if (!temp.contains("Недоступно")) {
                summary.append("<div class='summary-item'>");
                summary.append("<div class='summary-icon'>🌡</div>");
                summary.append("<div class='summary-text'>");
                summary.append("<div class='summary-label'>Температура CPU</div>");
                summary.append("<div class='summary-value'>").append(escapeHtml(temp)).append("</div>");
                if (!status.isEmpty()) {
                    summary.append("<div class='summary-sub'>").append(escapeHtml(status)).append("</div>");
                }
                summary.append("</div></div>");
            }
        }

        // RAM
        Map<String, String> ram = data.get("Оперативная память (RAM)");
        if (ram != null) {
            String total = ram.getOrDefault("Всего", "?");
            String used = ram.getOrDefault("Используется", "?");
            String load = ram.getOrDefault("Загрузка", "?");
            summary.append("<div class='summary-item'>");
            summary.append("<div class='summary-icon'>🧠</div>");
            summary.append("<div class='summary-text'>");
            summary.append("<div class='summary-label'>Оперативная память</div>");
            summary.append("<div class='summary-value'>").append(escapeHtml(total)).append("</div>");
            summary.append("<div class='summary-sub'>Используется: ").append(escapeHtml(used))
                    .append(" (").append(escapeHtml(load)).append(")</div>");
            summary.append("</div></div>");
        }

        // GPU
        Map<String, String> gpu = data.get("Видеокарта (GPU)");
        if (gpu != null) {
            String gpuName = gpu.getOrDefault("Название", gpu.getOrDefault("GPU #1 Название", "Неизвестно"));
            String vram = gpu.getOrDefault("VRAM", gpu.getOrDefault("GPU #1 VRAM", "?"));
            summary.append("<div class='summary-item'>");
            summary.append("<div class='summary-icon'>🎮</div>");
            summary.append("<div class='summary-text'>");
            summary.append("<div class='summary-label'>Видеокарта</div>");
            summary.append("<div class='summary-value'>").append(escapeHtml(gpuName)).append("</div>");
            summary.append("<div class='summary-sub'>Видеопамять: ").append(escapeHtml(vram)).append("</div>");
            summary.append("</div></div>");
        }

        // Диски
        Map<String, String> disks = data.get("Диски и хранилища");
        if (disks != null) {
            int diskCount = 0;
            long totalSize = 0;
            StringBuilder diskNames = new StringBuilder();
            for (Map.Entry<String, String> entry : disks.entrySet()) {
                if (entry.getKey().contains("модель")) {
                    diskCount++;
                    if (diskNames.length() > 0) diskNames.append(", ");
                    diskNames.append(entry.getValue());
                }
            }
            if (diskCount > 0) {
                summary.append("<div class='summary-item'>");
                summary.append("<div class='summary-icon'>💾</div>");
                summary.append("<div class='summary-text'>");
                summary.append("<div class='summary-label'>Диски (").append(diskCount).append(" шт.)</div>");
                summary.append("<div class='summary-value'>").append(escapeHtml(diskNames.toString())).append("</div>");
                summary.append("</div></div>");
            }
        }

        // Аккумулятор
        Map<String, String> battery = data.get("Аккумулятор");
        if (battery != null && !battery.containsKey("Аккумулятор")) {
            String charge = battery.getOrDefault("Текущий заряд", "?");
            String state = battery.getOrDefault("Состояние", "?");
            String time = battery.getOrDefault("Осталось времени", "");
            summary.append("<div class='summary-item'>");
            summary.append("<div class='summary-icon'>🔋</div>");
            summary.append("<div class='summary-text'>");
            summary.append("<div class='summary-label'>Аккумулятор</div>");
            summary.append("<div class='summary-value'>").append(escapeHtml(charge)).append("</div>");
            summary.append("<div class='summary-sub'>").append(escapeHtml(state));
            if (!time.isEmpty()) summary.append(" • ").append(escapeHtml(time));
            summary.append("</div></div>");
        }

        // Материнская плата
        Map<String, String> mb = data.get("Материнская плата");
        if (mb != null) {
            String manufacturer = mb.getOrDefault("Плата производитель", "Неизвестно");
            String model = mb.getOrDefault("Плата модель", "Неизвестно");
            summary.append("<div class='summary-item'>");
            summary.append("<div class='summary-icon'>🔌</div>");
            summary.append("<div class='summary-text'>");
            summary.append("<div class='summary-label'>Материнская плата</div>");
            summary.append("<div class='summary-value'>").append(escapeHtml(manufacturer)).append(" ").append(escapeHtml(model)).append("</div>");
            summary.append("</div></div>");
        }

        // Антивирус
        Map<String, String> security = data.get("Безопасность (антивирус и брандмауэр)");
        if (security != null) {
            String antivirus = security.getOrDefault("🛡 Антивирус", "");
            if (!antivirus.isEmpty() && !antivirus.contains("Информация недоступна")) {
                summary.append("<div class='summary-item'>");
                summary.append("<div class='summary-icon'>🛡</div>");
                summary.append("<div class='summary-text'>");
                summary.append("<div class='summary-label'>Антивирус</div>");
                summary.append("<div class='summary-value'>").append(escapeHtml(antivirus)).append("</div>");
                summary.append("</div></div>");
            }
        }

        return summary.toString();
    }

    public static void saveToHtml(Map<String, Map<String, String>> data, String filePath) throws IOException {
        StringBuilder html = new StringBuilder();
        String date = LocalDateTime.now().format(FMT);
        String summaryHtml = generateSummary(data);

        html.append("""
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Отчёт о системе</title>
                  <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    
                    html, body {
                      font-family: 'Segoe UI', 'Inter', Tahoma, sans-serif;
                      color: #e0e0e0;
                      background: #0f0f23;
                    }
                    
                    body {
                      min-height: 100vh;
                      background: radial-gradient(ellipse at top, #1a1a3e 0%, #0f0f23 50%, #0a0a1a 100%);
                      padding: 30px;
                    }
                    
                    .container { max-width: 900px; margin: 0 auto; }
                    
                    /* ⭐ КНОПКА ПЕЧАТИ/PDF */
                    .actions-bar {
                      position: sticky;
                      top: 10px;
                      z-index: 100;
                      display: flex;
                      justify-content: flex-end;
                      gap: 10px;
                      margin-bottom: 20px;
                    }
                    
                    .action-btn {
                      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
                      color: white;
                      border: none;
                      padding: 12px 24px;
                      border-radius: 10px;
                      font-size: 14px;
                      font-weight: 600;
                      cursor: pointer;
                      box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
                      transition: transform 0.2s, box-shadow 0.2s;
                      font-family: inherit;
                    }
                    
                    .action-btn:hover {
                      transform: translateY(-2px);
                      box-shadow: 0 6px 20px rgba(99, 102, 241, 0.6);
                    }
                    
                    .action-btn.secondary {
                      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                      box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
                    }
                    
                    .action-btn.secondary:hover {
                      box-shadow: 0 6px 20px rgba(16, 185, 129, 0.6);
                    }
                    
                    /* ЗАГОЛОВОК */
                    .header {
                      text-align: center;
                      padding: 40px 20px;
                      background: linear-gradient(135deg, #1e3a5f 0%, #2d1b69 100%);
                      border-radius: 20px;
                      margin-bottom: 30px;
                      box-shadow: 0 10px 40px rgba(0,0,0,0.4);
                      border: 1px solid rgba(139, 92, 246, 0.3);
                    }
                    
                    .header h1 {
                      font-size: 32px;
                      font-weight: 700;
                      color: #fff;
                      text-shadow: 0 0 30px rgba(139, 92, 246, 0.5);
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
                    
                    /* ⭐ КРАТКАЯ СВОДКА */
                    .summary-section {
                      background: linear-gradient(135deg, #1e1e42 0%, #2a1f5a 100%);
                      border: 1px solid rgba(139, 92, 246, 0.3);
                      border-radius: 20px;
                      padding: 25px;
                      margin-bottom: 30px;
                      box-shadow: 0 8px 30px rgba(0,0,0,0.4);
                    }
                    
                    .summary-title {
                      font-size: 22px;
                      color: #fff;
                      margin-bottom: 20px;
                      display: flex;
                      align-items: center;
                      gap: 10px;
                      font-weight: 700;
                    }
                    
                    .summary-title-icon {
                      font-size: 28px;
                    }
                    
                    .summary-grid {
                      display: grid;
                      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                      gap: 15px;
                    }
                    
                    .summary-item {
                      background: rgba(255,255,255,0.05);
                      border: 1px solid rgba(139, 92, 246, 0.2);
                      border-radius: 12px;
                      padding: 15px;
                      display: flex;
                      gap: 12px;
                      align-items: flex-start;
                    }
                    
                    .summary-icon {
                      font-size: 32px;
                      flex-shrink: 0;
                    }
                    
                    .summary-text {
                      flex: 1;
                      min-width: 0;
                    }
                    
                    .summary-label {
                      font-size: 11px;
                      color: #94a3b8;
                      text-transform: uppercase;
                      letter-spacing: 1px;
                      margin-bottom: 4px;
                      font-weight: 600;
                    }
                    
                    .summary-value {
                      font-size: 14px;
                      color: #fff;
                      font-weight: 600;
                      margin-bottom: 4px;
                      word-break: break-word;
                    }
                    
                    .summary-sub {
                      font-size: 12px;
                      color: #cbd5e1;
                      word-break: break-word;
                    }
                    
                    /* СЕКЦИИ */
                    .section {
                      background: #16162e;
                      border: 1px solid #2a2a4a;
                      border-radius: 16px;
                      margin-bottom: 20px;
                      overflow: hidden;
                      box-shadow: 0 4px 15px rgba(0,0,0,0.3);
                    }
                    
                    .section-header {
                      background: linear-gradient(90deg, #1e1e42 0%, #2a1f5a 100%);
                      padding: 16px 24px;
                      border-bottom: 1px solid #2a2a4a;
                    }
                    
                    .section-header h2 {
                      font-size: 16px;
                      font-weight: 600;
                      color: #c4b5fd;
                      letter-spacing: 0.5px;
                    }
                    
                    table { width: 100%; border-collapse: collapse; }
                    
                    td {
                      padding: 12px 24px;
                      border-bottom: 1px solid #1f1f3a;
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
                      border-top: 1px solid #1f1f3a;
                      background: #0a0a1a;
                      border-radius: 16px;
                    }
                    
                    .footer p { margin: 5px 0; }
                    .footer-heart { color: #ef4444; }
                    
                    ::-webkit-scrollbar { width: 12px; }
                    ::-webkit-scrollbar-track { background: #0a0a1a; }
                    ::-webkit-scrollbar-thumb {
                      background: #6366f1;
                      border-radius: 6px;
                    }
                    ::-webkit-scrollbar-thumb:hover { background: #818cf8; }
                    
                    /* ⭐ СТИЛИ ДЛЯ ПЕЧАТИ/PDF */
                    @media print {
                      html, body { 
                        background: white !important; 
                        color: #000 !important; 
                        padding: 0;
                        margin: 0;
                      }
                      
                      body {
                        padding: 15px;
                      }
                      
                      .container {
                        max-width: 100%;
                      }
                      
                      .actions-bar {
                        display: none !important;
                      }
                      
                      .header { 
                        background: #f5f5f5 !important;
                        border: 2px solid #4c1d95;
                        page-break-after: avoid;
                      }
                      
                      .header h1 { 
                        color: #1e1e42 !important; 
                        text-shadow: none;
                      }
                      
                      .header .subtitle,
                      .header .date {
                        color: #555 !important;
                      }
                      
                      .summary-section {
                        background: #f0f0f8 !important;
                        border: 2px solid #4c1d95 !important;
                        page-break-after: avoid;
                      }
                      
                      .summary-title {
                        color: #1e1e42 !important;
                      }
                      
                      .summary-item {
                        background: white !important;
                        border: 1px solid #ccc !important;
                        page-break-inside: avoid;
                      }
                      
                      .summary-label {
                        color: #555 !important;
                      }
                      
                      .summary-value {
                        color: #1e1e42 !important;
                      }
                      
                      .summary-sub {
                        color: #444 !important;
                      }
                      
                      .section { 
                        border: 1px solid #ccc !important; 
                        box-shadow: none !important; 
                        background: white !important;
                        page-break-inside: avoid;
                        margin-bottom: 15px;
                      }
                      
                      .section-header {
                        background: #e8e8f0 !important;
                        border-bottom: 1px solid #ccc !important;
                      }
                      
                      .section-header h2 { 
                        color: #4c1d95 !important; 
                      }
                      
                      td:first-child { 
                        color: #555 !important; 
                      }
                      
                      td:last-child { 
                        color: #000 !important; 
                      }
                      
                      tr {
                        page-break-inside: avoid;
                      }
                      
                      .footer {
                        background: #f5f5f5 !important;
                        color: #555 !important;
                        border: 1px solid #ccc !important;
                        page-break-before: avoid;
                      }
                    }
                    
                    @media (max-width: 768px) {
                      body { padding: 15px; }
                      .header h1 { font-size: 22px; }
                      td { padding: 10px 15px; font-size: 12px; }
                      .summary-grid { grid-template-columns: 1fr; }
                      .actions-bar { 
                        position: relative;
                        top: 0;
                        flex-direction: column;
                      }
                      .action-btn {
                        width: 100%;
                      }
                    }
                  </style>
                </head>
                <body>
                  <div class="container">
                    
                    <!-- ⭐ КНОПКИ ДЕЙСТВИЙ -->
                    <div class="actions-bar">
                      <button class="action-btn" onclick="window.print()">
                        🖨 Печать / Сохранить PDF
                      </button>
                      <button class="action-btn secondary" onclick="copyReport()">
                        📋 Копировать данные
                      </button>
                    </div>
                    
                    <div class="header">
                      <h1>🖥 Отчёт о характеристиках компьютера</h1>
                      <p class="subtitle">System Info • Данные собраны с согласия пользователя</p>
                """);

        html.append("      <p class=\"date\">📅 ").append(date).append("</p>\n");
        html.append("    </div>\n\n");

        // ⭐ КРАТКАЯ СВОДКА
        html.append("""
                    <div class="summary-section">
                      <div class="summary-title">
                        <span class="summary-title-icon">📊</span>
                        <span>Краткая сводка о системе</span>
                      </div>
                      <div class="summary-grid">
                """);
        html.append(summaryHtml);
        html.append("      </div>\n");
        html.append("    </div>\n\n");

        // Подробные секции
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
        html.append("      <p>Сделано с <span class=\"footer-heart\">♥</span> для удобной диагностики ПК</p>\n");
        html.append("    </div>\n");
        html.append("  </div>\n");

        // ⭐ JavaScript для копирования
        html.append("""
                  <script>
                    function copyReport() {
                      const sections = document.querySelectorAll('.section');
                      let text = '═══════════════════════════════════════════\\n';
                      text += '   ОТЧЁТ О ХАРАКТЕРИСТИКАХ КОМПЬЮТЕРА\\n';
                      text += '═══════════════════════════════════════════\\n\\n';
                      
                      sections.forEach(section => {
                        const title = section.querySelector('h2').innerText;
                        text += '── ' + title + ' ──\\n';
                        const rows = section.querySelectorAll('tr');
                        rows.forEach(row => {
                          const cells = row.querySelectorAll('td');
                          if (cells.length === 2) {
                            text += '  ' + cells[0].innerText.padEnd(32) + cells[1].innerText + '\\n';
                          }
                        });
                        text += '\\n';
                      });
                      
                      navigator.clipboard.writeText(text).then(() => {
                        const btn = event.target;
                        const original = btn.innerHTML;
                        btn.innerHTML = '✅ Скопировано!';
                        btn.style.background = 'linear-gradient(135deg, #10b981 0%, #059669 100%)';
                        setTimeout(() => {
                          btn.innerHTML = original;
                        }, 2000);
                      }).catch(err => {
                        alert('Не удалось скопировать: ' + err);
                      });
                    }
                  </script>
                </body>
                </html>""");

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
package com.systeminfo;

/**
 * Хранит настройки автоматического сохранения отчёта.
 * Формат всегда HTML.
 */
public class AutoSendSettings {

    private boolean enabled;
    private String destinationPath;
    private String filenamePattern;
    private boolean appendTimestamp;

    public AutoSendSettings() {
        this.enabled = false;
        this.destinationPath = "";
        this.filenamePattern = "system_report";
        this.appendTimestamp = true;
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getDestinationPath() { return destinationPath; }
    public void setDestinationPath(String destinationPath) { this.destinationPath = destinationPath; }

    public String getFilenamePattern() { return filenamePattern; }
    public void setFilenamePattern(String filenamePattern) { this.filenamePattern = filenamePattern; }

    public boolean isAppendTimestamp() { return appendTimestamp; }
    public void setAppendTimestamp(boolean appendTimestamp) { this.appendTimestamp = appendTimestamp; }

    /**
     * Генерирует полное имя файла. Формат всегда .html
     */
    public String buildFullFilePath() {
        StringBuilder filename = new StringBuilder();

        String path = destinationPath.trim();
        if (!path.endsWith(java.io.File.separator) && !path.endsWith("/")) {
            path += java.io.File.separator;
        }
        filename.append(path);
        filename.append(filenamePattern);

        if (appendTimestamp) {
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            filename.append("_").append(timestamp);
        }

        filename.append(".html");
        return filename.toString();
    }
}
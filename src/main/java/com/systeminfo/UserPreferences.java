package com.systeminfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Хранит настройки пользователя в файле .systeminfo_prefs в домашней папке.
 */
public class UserPreferences {

    private static final String CONFIG_FILE_NAME = ".systeminfo_prefs";
    private static final String KEY_AGREED = "user.agreed";
    private static final String KEY_AGREED_DATE = "user.agreed.date";
    private static final String KEY_AGREED_VERSION = "user.agreed.version";
    private static final String KEY_DEVICE_TYPE = "user.device.type";

    private static final String CURRENT_VERSION = "1.0";

    // Типы устройств
    public static final String DEVICE_DESKTOP = "desktop";
    public static final String DEVICE_LAPTOP = "laptop";

    private final Path configPath;
    private final Properties properties;

    public UserPreferences() {
        String userHome = System.getProperty("user.home");
        this.configPath = Paths.get(userHome, CONFIG_FILE_NAME);
        this.properties = new Properties();
        load();
    }

    private void load() {
        File file = configPath.toFile();
        if (file.exists()) {
            try (var input = Files.newInputStream(configPath)) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Не удалось загрузить настройки: " + e.getMessage());
            }
        }
    }

    private void save() {
        try (var output = Files.newOutputStream(configPath)) {
            properties.store(output, "System Info User Preferences");
        } catch (IOException e) {
            System.err.println("Не удалось сохранить настройки: " + e.getMessage());
        }
    }

    public boolean hasAgreedToTerms() {
        String agreed = properties.getProperty(KEY_AGREED, "false");
        String version = properties.getProperty(KEY_AGREED_VERSION, "");
        return "true".equalsIgnoreCase(agreed) && CURRENT_VERSION.equals(version);
    }

    public void setAgreedToTerms() {
        properties.setProperty(KEY_AGREED, "true");
        properties.setProperty(KEY_AGREED_VERSION, CURRENT_VERSION);
        properties.setProperty(KEY_AGREED_DATE,
                java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        save();
    }

    public void resetAgreement() {
        properties.remove(KEY_AGREED);
        properties.remove(KEY_AGREED_VERSION);
        properties.remove(KEY_AGREED_DATE);
        save();
    }

    public String getAgreementDate() {
        return properties.getProperty(KEY_AGREED_DATE, "неизвестно");
    }

    /**
     * Тип устройства (desktop / laptop).
     */
    public String getDeviceType() {
        return properties.getProperty(KEY_DEVICE_TYPE, "");
    }

    public void setDeviceType(String deviceType) {
        properties.setProperty(KEY_DEVICE_TYPE, deviceType);
        save();
    }

    public boolean hasDeviceType() {
        String type = getDeviceType();
        return DEVICE_DESKTOP.equals(type) || DEVICE_LAPTOP.equals(type);
    }

    public boolean isLaptop() {
        return DEVICE_LAPTOP.equals(getDeviceType());
    }

    public boolean isDesktop() {
        return DEVICE_DESKTOP.equals(getDeviceType());
    }
}
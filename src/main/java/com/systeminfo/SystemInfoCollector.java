package com.systeminfo;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.OperatingSystem;

import java.time.LocalDate;
import java.util.*;

public class SystemInfoCollector {

    private final SystemInfo si = new SystemInfo();
    private final HardwareAbstractionLayer hal = si.getHardware();
    private final OperatingSystem os = si.getOperatingSystem();

    public Map<String, Map<String, String>> collect(
            boolean collectOS, boolean collectCPU, boolean collectRAM,
            boolean collectGPU, boolean collectDisks, boolean collectNetwork,
            boolean collectMotherboard, boolean collectDisplay, boolean collectJava,
            boolean collectBattery, boolean collectSensors, boolean collectConnections,
            boolean collectSecurity
    ) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        if (collectOS) result.put("Операционная система", getOSInfo());
        if (collectCPU) result.put("Процессор (CPU)", getCPUInfo());
        if (collectSensors) result.put("Датчики (температура)", getSensorsInfo());
        if (collectRAM) result.put("Оперативная память (RAM)", getRAMInfo());
        if (collectGPU) result.put("Видеокарта (GPU)", getGPUInfo());
        if (collectDisks) result.put("Диски и хранилища", getDiskInfo());
        if (collectNetwork) result.put("Сетевые интерфейсы", getNetworkInfo());
        if (collectConnections) result.put("Активные сетевые соединения", getActiveConnections());
        if (collectMotherboard) result.put("Материнская плата", getMotherboardInfo());
        if (collectDisplay) result.put("Дисплей", getDisplayInfo());
        if (collectBattery) result.put("Аккумулятор", getBatteryInfo());
        if (collectSecurity) result.put("Безопасность (антивирус и брандмауэр)", getSecurityInfo());
        if (collectJava) result.put("Среда Java", getJavaInfo());

        return result;
    }

    // Перегруженный метод для обратной совместимости
    public Map<String, Map<String, String>> collect(
            boolean collectOS, boolean collectCPU, boolean collectRAM,
            boolean collectGPU, boolean collectDisks, boolean collectNetwork,
            boolean collectMotherboard, boolean collectDisplay, boolean collectJava
    ) {
        return collect(collectOS, collectCPU, collectRAM, collectGPU, collectDisks,
                collectNetwork, collectMotherboard, collectDisplay, collectJava,
                true, true, true, true);
    }

    private Map<String, String> getOSInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            info.put("Название ОС", os.toString());
            info.put("Семейство", os.getFamily());
            info.put("Производитель", os.getManufacturer());
            info.put("Версия", os.getVersionInfo().toString());
            info.put("Битность", os.getBitness() + "-bit");
            info.put("Имя компьютера", os.getNetworkParams().getHostName());
            info.put("Время работы (uptime)", formatUptime(os.getSystemUptime()));
            info.put("Количество процессов", String.valueOf(os.getProcessCount()));
            info.put("Количество потоков", String.valueOf(os.getThreadCount()));
        } catch (Exception e) { info.put("Ошибка", e.getMessage()); }
        return info;
    }

    private Map<String, String> getCPUInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            CentralProcessor cpu = hal.getProcessor();
            CentralProcessor.ProcessorIdentifier id = cpu.getProcessorIdentifier();
            info.put("Название", id.getName().trim());
            info.put("Производитель", id.getVendor());
            info.put("Идентификатор", id.getIdentifier());
            info.put("Микроархитектура", id.getMicroarchitecture());
            info.put("Физические ядра", String.valueOf(cpu.getPhysicalProcessorCount()));
            info.put("Логические ядра", String.valueOf(cpu.getLogicalProcessorCount()));
            info.put("Физические пакеты", String.valueOf(cpu.getPhysicalPackageCount()));
            info.put("Макс. частота", String.format("%.2f ГГц", cpu.getMaxFreq() / 1_000_000_000.0));
            info.put("64-bit", id.isCpu64bit() ? "Да" : "Нет");

            long[] prevTicks = cpu.getSystemCpuLoadTicks();
            Thread.sleep(1000);
            double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
            info.put("Текущая загрузка", String.format("%.1f%%", cpuLoad));
        } catch (Exception e) { info.put("Ошибка", e.getMessage()); }
        return info;
    }

    // ⭐ НОВЫЙ МЕТОД: Датчики (температура, вентиляторы)
    private Map<String, String> getSensorsInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            Sensors sensors = hal.getSensors();

            // Температура CPU
            double cpuTemp = sensors.getCpuTemperature();
            if (cpuTemp > 0) {
                info.put("Температура CPU", String.format("%.1f °C", cpuTemp));

                String status;
                if (cpuTemp < 50) status = "✅ Нормальная";
                else if (cpuTemp < 70) status = "⚠ Повышенная";
                else if (cpuTemp < 85) status = "⚠ Высокая";
                else status = "🔥 КРИТИЧЕСКАЯ!";
                info.put("Статус температуры", status);
            } else {
                info.put("Температура CPU", "Недоступно (датчик не найден)");
            }

            // Напряжение CPU
            double voltage = sensors.getCpuVoltage();
            if (voltage > 0) {
                info.put("Напряжение CPU", String.format("%.2f В", voltage));
            }

            // Скорости вентиляторов
            int[] fanSpeeds = sensors.getFanSpeeds();
            if (fanSpeeds != null && fanSpeeds.length > 0) {
                for (int i = 0; i < fanSpeeds.length; i++) {
                    if (fanSpeeds[i] > 0) {
                        info.put("Вентилятор #" + (i + 1), fanSpeeds[i] + " RPM");
                    }
                }
            } else {
                info.put("Вентиляторы", "Недоступно");
            }

            if (info.isEmpty()) {
                info.put("Информация", "Датчики недоступны на данной системе");
                info.put("Примечание", "Для получения температуры может потребоваться запуск от имени администратора");
            }

        } catch (Exception e) {
            info.put("Ошибка", e.getMessage());
            info.put("Примечание", "Запустите программу от имени администратора для доступа к датчикам");
        }
        return info;
    }

    private Map<String, String> getRAMInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            GlobalMemory memory = hal.getMemory();
            long total = memory.getTotal();
            long available = memory.getAvailable();
            long used = total - available;
            info.put("Всего", formatBytes(total));
            info.put("Используется", formatBytes(used));
            info.put("Доступно", formatBytes(available));
            info.put("Загрузка", String.format("%.1f%%", (double) used / total * 100));

            VirtualMemory vm = memory.getVirtualMemory();
            info.put("Swap всего", formatBytes(vm.getSwapTotal()));
            info.put("Swap используется", formatBytes(vm.getSwapUsed()));

            List<PhysicalMemory> pmList = memory.getPhysicalMemory();
            int idx = 1;
            for (PhysicalMemory pm : pmList) {
                String p = "Планка #" + idx;
                info.put(p + " производитель", pm.getManufacturer());
                info.put(p + " объём", formatBytes(pm.getCapacity()));
                info.put(p + " тип", pm.getMemoryType());
                info.put(p + " частота", String.format("%.0f МГц", pm.getClockSpeed() / 1_000_000.0));
                info.put(p + " банк", pm.getBankLabel());
                idx++;
            }
        } catch (Exception e) { info.put("Ошибка", e.getMessage()); }
        return info;
    }

    private Map<String, String> getGPUInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            List<GraphicsCard> gpus = hal.getGraphicsCards();
            if (gpus.isEmpty()) { info.put("GPU", "Не обнаружена"); return info; }
            int idx = 1;
            for (GraphicsCard gpu : gpus) {
                String p = gpus.size() > 1 ? "GPU #" + idx + " " : "";
                info.put(p + "Название", gpu.getName());
                info.put(p + "Производитель", gpu.getVendor());
                info.put(p + "VRAM", formatBytes(gpu.getVRam()));
                info.put(p + "Версия драйвера", gpu.getVersionInfo());
                info.put(p + "Device ID", gpu.getDeviceId());
                idx++;
            }
        } catch (Exception e) { info.put("Ошибка", e.getMessage()); }
        return info;
    }

    private Map<String, String> getDiskInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            List<HWDiskStore> disks = hal.getDiskStores();
            int idx = 1;
            for (HWDiskStore disk : disks) {
                String p = "Диск #" + idx;
                info.put(p + " модель", disk.getModel());
                info.put(p + " серийный", disk.getSerial().trim());
                info.put(p + " размер", formatBytes(disk.getSize()));
                info.put(p + " чтений", formatBytes(disk.getReadBytes()));
                info.put(p + " записей", formatBytes(disk.getWriteBytes()));
                int pi = 1;
                for (HWPartition part : disk.getPartitions()) {
                    String pp = p + " раздел " + pi;
                    info.put(pp + " имя", part.getName());
                    info.put(pp + " тип", part.getType());
                    info.put(pp + " размер", formatBytes(part.getSize()));
                    info.put(pp + " точка монтирования", part.getMountPoint());
                    pi++;
                }
                idx++;
            }
        } catch (Exception e) { info.put("Ошибка", e.getMessage()); }
        return info;
    }

    private Map<String, String> getNetworkInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            List<NetworkIF> nets = hal.getNetworkIFs();
            int idx = 1;
            for (NetworkIF net : nets) {
                String p = "IF #" + idx;
                info.put(p + " имя", net.getName());
                info.put(p + " описание", net.getDisplayName());
                info.put(p + " MAC", net.getMacaddr());
                info.put(p + " скорость", net.getSpeed() > 0
                        ? String.format("%d Мбит/с", net.getSpeed() / 1_000_000) : "N/A");
                if (net.getIPv4addr().length > 0)
                    info.put(p + " IPv4", String.join(", ", net.getIPv4addr()));
                if (net.getIPv6addr().length > 0)
                    info.put(p + " IPv6", String.join(", ", net.getIPv6addr()));
                info.put(p + " отправлено", formatBytes(net.getBytesSent()));
                info.put(p + " получено", formatBytes(net.getBytesRecv()));
                idx++;
            }
            String[] dns = os.getNetworkParams().getDnsServers();
            if (dns.length > 0) info.put("DNS-серверы", String.join(", ", dns));
            info.put("Шлюз", os.getNetworkParams().getIpv4DefaultGateway());
        } catch (Exception e) { info.put("Ошибка", e.getMessage()); }
        return info;
    }

    // ⭐ НОВЫЙ МЕТОД: Активные сетевые соединения
    private Map<String, String> getActiveConnections() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            InternetProtocolStats ipStats = os.getInternetProtocolStats();

            // TCP статистика
            InternetProtocolStats.TcpStats tcp4 = ipStats.getTCPv4Stats();
            info.put("📊 TCP v4 — Активных соединений", String.valueOf(tcp4.getConnectionsEstablished()));
            info.put("📊 TCP v4 — Прослушивающих портов", String.valueOf(tcp4.getConnectionsActive()));
            info.put("📊 TCP v4 — Сегментов отправлено", String.valueOf(tcp4.getSegmentsSent()));
            info.put("📊 TCP v4 — Сегментов получено", String.valueOf(tcp4.getSegmentsReceived()));
            info.put("📊 TCP v4 — Ошибок отправки", String.valueOf(tcp4.getOutResets()));

            InternetProtocolStats.TcpStats tcp6 = ipStats.getTCPv6Stats();
            info.put("📊 TCP v6 — Активных соединений", String.valueOf(tcp6.getConnectionsEstablished()));

            // UDP статистика
            InternetProtocolStats.UdpStats udp4 = ipStats.getUDPv4Stats();
            info.put("📊 UDP v4 — Датаграмм отправлено", String.valueOf(udp4.getDatagramsSent()));
            info.put("📊 UDP v4 — Датаграмм получено", String.valueOf(udp4.getDatagramsReceived()));

            // Список активных соединений
            List<InternetProtocolStats.IPConnection> connections = ipStats.getConnections();
            int established = 0;
            int listening = 0;
            int timeWait = 0;

            for (InternetProtocolStats.IPConnection conn : connections) {
                String state = conn.getState().toString();
                if (state.contains("ESTABLISHED")) established++;
                else if (state.contains("LISTEN")) listening++;
                else if (state.contains("TIME_WAIT")) timeWait++;
            }

            info.put("🔗 Всего соединений", String.valueOf(connections.size()));
            info.put("✅ Установленных (ESTABLISHED)", String.valueOf(established));
            info.put("👂 Прослушивающих (LISTEN)", String.valueOf(listening));
            info.put("⏳ Ожидающих закрытия (TIME_WAIT)", String.valueOf(timeWait));

            // Показываем первые 15 активных соединений
            int count = 0;
            int maxShow = 15;
            for (InternetProtocolStats.IPConnection conn : connections) {
                if (!conn.getState().toString().contains("ESTABLISHED")) continue;
                if (count >= maxShow) break;

                String localAddr = formatAddress(conn.getLocalAddress()) + ":" + conn.getLocalPort();
                String foreignAddr = formatAddress(conn.getForeignAddress()) + ":" + conn.getForeignPort();
                info.put("Соединение #" + (count + 1),
                        localAddr + " → " + foreignAddr + " [" + conn.getType() + "]");
                count++;
            }

            if (established > maxShow) {
                info.put("Примечание", "Показано " + maxShow + " из " + established + " активных соединений");
            }

        } catch (Exception e) {
            info.put("Ошибка", e.getMessage());
        }
        return info;
    }

    private String formatAddress(byte[] addr) {
        if (addr == null || addr.length == 0) return "0.0.0.0";
        try {
            if (addr.length == 4) {
                return String.format("%d.%d.%d.%d",
                        addr[0] & 0xFF, addr[1] & 0xFF, addr[2] & 0xFF, addr[3] & 0xFF);
            } else {
                return java.net.InetAddress.getByAddress(addr).getHostAddress();
            }
        } catch (Exception e) {
            return "?.?.?.?";
        }
    }

    private Map<String, String> getMotherboardInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            ComputerSystem cs = hal.getComputerSystem();
            info.put("Производитель ПК", cs.getManufacturer());
            info.put("Модель ПК", cs.getModel());
            info.put("Серийный номер", cs.getSerialNumber());
            info.put("UUID", cs.getHardwareUUID());
            Baseboard bb = cs.getBaseboard();
            info.put("Плата производитель", bb.getManufacturer());
            info.put("Плата модель", bb.getModel());
            info.put("Плата версия", bb.getVersion());
            info.put("Плата серийный", bb.getSerialNumber());
            Firmware fw = cs.getFirmware();
            info.put("BIOS производитель", fw.getManufacturer());
            info.put("BIOS название", fw.getName());
            info.put("BIOS версия", fw.getVersion());
            info.put("BIOS дата", fw.getReleaseDate());
        } catch (Exception e) { info.put("Ошибка", e.getMessage()); }
        return info;
    }

    private Map<String, String> getDisplayInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            List<Display> displays = hal.getDisplays();
            int idx = 1;
            for (Display d : displays) {
                info.put("Дисплей #" + idx + " EDID", d.toString().trim());
                idx++;
            }
            java.awt.GraphicsDevice[] screens = java.awt.GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getScreenDevices();
            for (int i = 0; i < screens.length; i++) {
                java.awt.DisplayMode dm = screens[i].getDisplayMode();
                String p = screens.length > 1 ? "Экран #" + (i + 1) + " " : "";
                info.put(p + "Разрешение", dm.getWidth() + " x " + dm.getHeight());
                info.put(p + "Частота", dm.getRefreshRate() + " Гц");
                info.put(p + "Глубина цвета", dm.getBitDepth() + " бит");
            }
        } catch (Exception e) { info.put("Ошибка", e.getMessage()); }
        return info;
    }

    // ⭐ НОВЫЙ МЕТОД: Аккумулятор
    private Map<String, String> getBatteryInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            List<PowerSource> powerSources = hal.getPowerSources();

            if (powerSources.isEmpty()) {
                info.put("Аккумулятор", "Не обнаружен (стационарный ПК или ноутбук без батареи)");
                return info;
            }

            int idx = 1;
            for (PowerSource ps : powerSources) {
                String prefix = powerSources.size() > 1 ? "Батарея #" + idx + " " : "";

                info.put(prefix + "Название", ps.getName());
                info.put(prefix + "Производитель", ps.getManufacturer());
                info.put(prefix + "Модель", ps.getDeviceName());

                // Заряд
                double charge = ps.getRemainingCapacityPercent() * 100;
                info.put(prefix + "Текущий заряд", String.format("%.1f%%", charge));

                // Статус заряда
                String chargeStatus;
                if (charge >= 80) chargeStatus = "🔋 Полный";
                else if (charge >= 50) chargeStatus = "🔋 Средний";
                else if (charge >= 20) chargeStatus = "🪫 Низкий";
                else chargeStatus = "⚠ КРИТИЧЕСКИЙ!";
                info.put(prefix + "Статус заряда", chargeStatus);

                // Состояние
                if (ps.isCharging()) {
                    info.put(prefix + "Состояние", "⚡ Заряжается");
                } else if (ps.isDischarging()) {
                    info.put(prefix + "Состояние", "🔌 Разряжается (от батареи)");
                } else if (ps.isPowerOnLine()) {
                    info.put(prefix + "Состояние", "🔌 Питание от сети");
                } else {
                    info.put(prefix + "Состояние", "❓ Неизвестно");
                }

                // Время работы
                double timeRemaining = ps.getTimeRemainingEstimated();
                if (timeRemaining > 0) {
                    int hours = (int) (timeRemaining / 3600);
                    int minutes = (int) ((timeRemaining % 3600) / 60);
                    info.put(prefix + "Осталось времени", hours + " ч. " + minutes + " мин.");
                } else if (timeRemaining == -1.0) {
                    info.put(prefix + "Осталось времени", "Рассчитывается...");
                } else if (timeRemaining == -2.0) {
                    info.put(prefix + "Осталось времени", "Заряжается от сети");
                }

                // Ёмкость
                double maxCapacity = ps.getMaxCapacity();
                double designCapacity = ps.getDesignCapacity();
                if (maxCapacity > 0) {
                    info.put(prefix + "Текущая ёмкость", String.format("%.0f мВт·ч", maxCapacity));
                }
                if (designCapacity > 0) {
                    info.put(prefix + "Проектная ёмкость", String.format("%.0f мВт·ч", designCapacity));

                    // Износ батареи
                    if (maxCapacity > 0) {
                        double wear = 100.0 - (maxCapacity / designCapacity * 100);
                        info.put(prefix + "Износ батареи", String.format("%.1f%%", wear));

                        String wearStatus;
                        if (wear < 10) wearStatus = "✅ Отличное состояние";
                        else if (wear < 25) wearStatus = "✅ Хорошее";
                        else if (wear < 40) wearStatus = "⚠ Среднее";
                        else wearStatus = "⚠ Износ значительный";
                        info.put(prefix + "Состояние батареи", wearStatus);
                    }
                }

                // Напряжение
                double voltage = ps.getVoltage();
                if (voltage > 0) {
                    info.put(prefix + "Напряжение", String.format("%.2f В", voltage));
                }

                // Сила тока
                double amperage = ps.getAmperage();
                if (amperage != 0) {
                    info.put(prefix + "Сила тока", String.format("%.0f мА", amperage));
                }

                // Температура батареи
                double temperature = ps.getTemperature();
                if (temperature > 0) {
                    info.put(prefix + "Температура батареи", String.format("%.1f °C", temperature));
                }

                // Циклы зарядки
                int cycleCount = ps.getCycleCount();
                if (cycleCount > 0) {
                    info.put(prefix + "Циклов зарядки", String.valueOf(cycleCount));
                }

                // Химия
                String chemistry = ps.getChemistry();
                if (chemistry != null && !chemistry.isEmpty()) {
                    info.put(prefix + "Химический состав", chemistry);
                }

                // Дата производства
                LocalDate manufactureDate = ps.getManufactureDate();
                if (manufactureDate != null) {
                    info.put(prefix + "Дата производства", manufactureDate.toString());
                }

                idx++;
            }
        } catch (Exception e) {
            info.put("Ошибка", e.getMessage());
        }
        return info;
    }

    // ⭐ НОВЫЙ МЕТОД: Безопасность (антивирус и брандмауэр)
    private Map<String, String> getSecurityInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        try {
            // Информация об антивирусе (только Windows через WMI)
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("win")) {
                // Получаем антивирус через PowerShell
                String antivirusInfo = getWindowsAntivirusInfo();
                if (antivirusInfo != null && !antivirusInfo.isEmpty()) {
                    info.put("🛡 Антивирус", antivirusInfo);
                } else {
                    info.put("🛡 Антивирус", "Информация недоступна");
                }

                // Статус брандмауэра Windows
                String firewallStatus = getWindowsFirewallStatus();
                if (firewallStatus != null) {
                    info.put("🔥 Брандмауэр Windows", firewallStatus);
                }

                // Защитник Windows
                String defenderStatus = getWindowsDefenderStatus();
                if (defenderStatus != null) {
                    info.put("🛡 Защитник Windows", defenderStatus);
                }

                // UAC статус
                info.put("👤 User Account Control (UAC)", checkUAC());

            } else if (osName.contains("linux")) {
                info.put("Брандмауэр", "Linux — проверьте iptables/ufw вручную");
                info.put("Антивирус", "Linux — обычно не требуется");
            } else if (osName.contains("mac")) {
                info.put("Брандмауэр", "macOS встроенный");
            }

        } catch (Exception e) {
            info.put("Ошибка", e.getMessage());
        }
        return info;
    }

    private String getWindowsAntivirusInfo() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-Command",
                    "Get-CimInstance -Namespace 'root\\SecurityCenter2' -ClassName 'AntiVirusProduct' | Select-Object -ExpandProperty displayName"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            StringBuilder result = new StringBuilder();
            try (var reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(p.getInputStream(), "Cp866"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        if (result.length() > 0) result.append(", ");
                        result.append(line.trim());
                    }
                }
            }

            p.waitFor();

            String output = result.toString().trim();
            return output.isEmpty() ? "Не обнаружен" : output;

        } catch (Exception e) {
            return null;
        }
    }

    private String getWindowsFirewallStatus() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-Command",
                    "Get-NetFirewallProfile | Select-Object Name, Enabled | Format-Table -HideTableHeaders"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            StringBuilder result = new StringBuilder();
            try (var reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(p.getInputStream(), "Cp866"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length >= 2) {
                            String profile = parts[0];
                            String enabled = parts[1].equalsIgnoreCase("True") ? "✅ Включён" : "❌ Выключен";
                            if (result.length() > 0) result.append(" | ");
                            result.append(profile).append(": ").append(enabled);
                        }
                    }
                }
            }

            p.waitFor();
            return result.length() > 0 ? result.toString() : "Статус неизвестен";

        } catch (Exception e) {
            return "Ошибка получения статуса";
        }
    }

    private String getWindowsDefenderStatus() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-Command",
                    "Get-MpComputerStatus | Select-Object -ExpandProperty AntivirusEnabled"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            try (var reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(p.getInputStream(), "Cp866"))) {
                String line = reader.readLine();
                p.waitFor();
                if (line != null) {
                    return line.trim().equalsIgnoreCase("True") ? "✅ Включён" : "❌ Выключен";
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String checkUAC() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "reg", "query",
                    "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System",
                    "/v", "EnableLUA"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            try (var reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(p.getInputStream(), "Cp866"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("EnableLUA")) {
                        if (line.contains("0x1")) return "✅ Включён";
                        else if (line.contains("0x0")) return "❌ Выключен";
                    }
                }
            }
            p.waitFor();
        } catch (Exception e) {
            return "Не удалось определить";
        }
        return "Не удалось определить";
    }

    private Map<String, String> getJavaInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Java версия", System.getProperty("java.version"));
        info.put("Java vendor", System.getProperty("java.vendor"));
        info.put("Java home", System.getProperty("java.home"));
        info.put("JVM", System.getProperty("java.vm.name"));
        info.put("JVM версия", System.getProperty("java.vm.version"));
        info.put("Кодировка", System.getProperty("file.encoding"));
        info.put("Пользователь", System.getProperty("user.name"));
        info.put("Домашняя папка", System.getProperty("user.home"));
        info.put("Рабочая папка", System.getProperty("user.dir"));
        Runtime rt = Runtime.getRuntime();
        info.put("JVM макс. память", formatBytes(rt.maxMemory()));
        info.put("JVM выделено", formatBytes(rt.totalMemory()));
        info.put("JVM свободно", formatBytes(rt.freeMemory()));
        info.put("Доступные процессоры", String.valueOf(rt.availableProcessors()));
        return info;
    }

    public static String formatBytes(long bytes) {
        if (bytes < 0) return "N/A";
        if (bytes == 0) return "0 Б";
        String[] units = {"Б", "КБ", "МБ", "ГБ", "ТБ"};
        int i = 0;
        double val = bytes;
        while (val >= 1024 && i < units.length - 1) { val /= 1024.0; i++; }
        return String.format("%.2f %s", val, units[i]);
    }

    private String formatUptime(long sec) {
        long d = sec / 86400, h = (sec % 86400) / 3600, m = (sec % 3600) / 60, s = sec % 60;
        StringBuilder sb = new StringBuilder();
        if (d > 0) sb.append(d).append(" дн. ");
        if (h > 0) sb.append(h).append(" ч. ");
        if (m > 0) sb.append(m).append(" мин. ");
        sb.append(s).append(" сек.");
        return sb.toString();
    }
}
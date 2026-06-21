package com.systeminfo;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.util.*;

public class SystemInfoCollector {

    private final SystemInfo si = new SystemInfo();
    private final HardwareAbstractionLayer hal = si.getHardware();
    private final OperatingSystem os = si.getOperatingSystem();

    public Map<String, Map<String, String>> collect(
            boolean collectOS, boolean collectCPU, boolean collectRAM,
            boolean collectGPU, boolean collectDisks, boolean collectNetwork,
            boolean collectMotherboard, boolean collectDisplay, boolean collectJava
    ) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        if (collectOS) result.put("Операционная система", getOSInfo());
        if (collectCPU) result.put("Процессор (CPU)", getCPUInfo());
        if (collectRAM) result.put("Оперативная память (RAM)", getRAMInfo());
        if (collectGPU) result.put("Видеокарта (GPU)", getGPUInfo());
        if (collectDisks) result.put("Диски и хранилища", getDiskInfo());
        if (collectNetwork) result.put("Сетевые интерфейсы", getNetworkInfo());
        if (collectMotherboard) result.put("Материнская плата", getMotherboardInfo());
        if (collectDisplay) result.put("Дисплей", getDisplayInfo());
        if (collectJava) result.put("Среда Java", getJavaInfo());

        return result;
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
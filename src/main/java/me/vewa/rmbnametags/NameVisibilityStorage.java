package me.vewa.rmbnametags;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Класс для сохранения состояния видимости ников игроков
 */
public class NameVisibilityStorage {
    private final JavaPlugin plugin;
    private final File storageFile;
    private FileConfiguration storage;
    private Set<UUID> visibleNames = new HashSet<>();

    public NameVisibilityStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.storageFile = new File(plugin.getDataFolder(), "visible_names.yml");
        loadStorage();
    }

    /**
     * Загружает данные о видимых никах из файла
     */
    private void loadStorage() {
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать файл visible_names.yml: " + e.getMessage());
            }
        }

        storage = YamlConfiguration.loadConfiguration(storageFile);
        List<String> visibleUUIDs = storage.getStringList("visible_names");
        visibleNames = visibleUUIDs.stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    /**
     * Сохраняет данные о видимых никах в файл
     */
    public void saveStorage() {
        List<String> visibleUUIDs = visibleNames.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
        
        storage.set("visible_names", visibleUUIDs);
        
        try {
            storage.save(storageFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить файл visible_names.yml: " + e.getMessage());
        }
    }

    /**
     * Добавляет игрока в список с видимым ником
     * @param uuid UUID игрока
     */
    public void addVisibleName(UUID uuid) {
        visibleNames.add(uuid);
        saveStorage();
    }

    /**
     * Удаляет игрока из списка с видимым ником
     * @param uuid UUID игрока
     */
    public void removeVisibleName(UUID uuid) {
        visibleNames.remove(uuid);
        saveStorage();
    }

    /**
     * Проверяет, есть ли игрок в списке с видимым ником
     * @param uuid UUID игрока
     * @return true, если ник игрока видимый
     */
    public boolean isNameVisible(UUID uuid) {
        return visibleNames.contains(uuid);
    }

    /**
     * Возвращает множество UUID игроков с видимыми никами
     * @return Множество UUID
     */
    public Set<UUID> getVisibleNames() {
        return new HashSet<>(visibleNames);
    }
} 
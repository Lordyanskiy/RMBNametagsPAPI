package me.vewa.rmbnametags;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RMBNametags extends JavaPlugin implements Listener {

    private ScoreboardManager manager;
    private Scoreboard board;
    private Team hiddenNamesTeam;
    private int displayTime;
    private String nameFormat;
    private boolean useInvisibleCharacter;
    private String invisibleCharacter;
    private boolean hideInTabList;
    
    // Хранилище для видимости ников игроков
    private NameVisibilityStorage nameVisibilityStorage;
    
    // Сообщения из конфигурации
    private Map<String, String> messages = new HashMap<>();
    
    // Константы для невидимых символов
    private static final String ZWSP = "\u200B"; // Zero-Width Space
    private static final String NBSP = "\u00A0"; // Non-Breaking Space
    private static final String ZWNJ = "\u200C"; // Zero-Width Non-Joiner
    private static final String ZWJ = "\u200D";  // Zero-Width Joiner

    @Override
    public void onEnable() {
        saveDefaultConfig();
        nameVisibilityStorage = new NameVisibilityStorage(this);
        loadConfig();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            Bukkit.getPluginManager().registerEvents(this, this);
        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("rmbnametags_reload").setExecutor(new ReloadCommand(this));
        getCommand("rmbnametags_toggle").setExecutor(new ToggleCommand(this));
        getCommand("rmbnametags_toggleall").setExecutor(new ToggleAllCommand(this));

        manager = Bukkit.getScoreboardManager();
        board = manager.getMainScoreboard();
        hiddenNamesTeam = board.getTeam("hiddenNames");

        if (hiddenNamesTeam == null) {
            hiddenNamesTeam = board.registerNewTeam("hiddenNames");
        }

        hiddenNamesTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        hiddenNamesTeam.setCanSeeFriendlyInvisibles(false);

        for (Player player: Bukkit.getOnlinePlayers()) {
            updatePlayerNameVisibility(player);
        }
    }

    @Override
    public void onDisable() {
        // Восстанавливаем оригинальные имена игроков перед выключением плагина
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hiddenNamesTeam.hasEntry(player.getName())) {
                player.setDisplayName(player.getName());
                player.setPlayerListName(player.getName());
            }
        }
        
        if (hiddenNamesTeam != null) {
            hiddenNamesTeam.unregister();
        }
    }

    public void loadConfig() {
        reloadConfig();
        FileConfiguration config = getConfig();
        displayTime = config.getInt("display-time", 3);
        nameFormat = ChatColor.translateAlternateColorCodes('&', config.getString("name-format", "&6%player_name%"));
        useInvisibleCharacter = config.getBoolean("use-invisible-character", true);
        hideInTabList = config.getBoolean("hide-in-tab-list", true);
        
        // Загружаем тип невидимого символа
        String charType = config.getString("invisible-character-type", "ZWSP");
        switch (charType.toUpperCase()) {
            case "NBSP":
                invisibleCharacter = NBSP;
                break;
            case "ZWNJ":
                invisibleCharacter = ZWNJ;
                break;
            case "ZWJ":
                invisibleCharacter = ZWJ;
                break;
            case "ZWSP":
            default:
                invisibleCharacter = ZWSP;
                break;
        }
        
        // Загружаем сообщения
        loadMessages();
        
        // Обновляем имена всех игроков при перезагрузке конфигурации
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerNameVisibility(player);
        }
    }
    
    /**
     * Загружает сообщения из конфигурации
     */
    private void loadMessages() {
        messages.clear();
        FileConfiguration config = getConfig();
        
        if (config.isConfigurationSection("messages")) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                String message = config.getString("messages." + key);
                if (message != null) {
                    messages.put(key, ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }
        
        // Устанавливаем значения по умолчанию для отсутствующих сообщений
        if (!messages.containsKey("reload-success")) {
            messages.put("reload-success", ChatColor.GREEN + "RMBNametags: Конфигурация успешно перезагружена!");
        }
        if (!messages.containsKey("name-visible")) {
            messages.put("name-visible", ChatColor.GREEN + "Ник игрока " + ChatColor.WHITE + "%player_name%" + ChatColor.GREEN + " теперь виден.");
        }
        if (!messages.containsKey("name-hidden")) {
            messages.put("name-hidden", ChatColor.GREEN + "Ник игрока " + ChatColor.WHITE + "%player_name%" + ChatColor.GREEN + " теперь скрыт.");
        }
        if (!messages.containsKey("all-names-visible")) {
            messages.put("all-names-visible", ChatColor.GREEN + "Ники всех игроков теперь видны.");
        }
        if (!messages.containsKey("all-names-hidden")) {
            messages.put("all-names-hidden", ChatColor.GREEN + "Ники всех игроков теперь скрыты.");
        }
        if (!messages.containsKey("player-not-found")) {
            messages.put("player-not-found", ChatColor.RED + "Игрок " + ChatColor.WHITE + "%player_name%" + ChatColor.RED + " не найден или не в сети.");
        }
        if (!messages.containsKey("no-permission")) {
            messages.put("no-permission", ChatColor.RED + "У вас нет прав для использования этой команды.");
        }
    }
    
    /**
     * Возвращает сообщение из конфигурации с заменой плейсхолдеров
     * @param key Ключ сообщения
     * @param replacements Пары плейсхолдер-значение для замены
     * @return Форматированное сообщение
     */
    public String getMessage(String key, Object... replacements) {
        String message = messages.getOrDefault(key, "");
        
        if (message.isEmpty()) {
            return "";
        }
        
        if (replacements != null && replacements.length > 0) {
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    String placeholder = String.valueOf(replacements[i]);
                    String value = String.valueOf(replacements[i + 1]);
                    message = message.replace(placeholder, value);
                }
            }
        }
        
        return message;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerNameVisibility(event.getPlayer());
    }

    /**
     * Обновляет видимость ника игрока в соответствии с настройками
     * @param player Игрок
     */
    private void updatePlayerNameVisibility(Player player) {
        if (nameVisibilityStorage.isNameVisible(player.getUniqueId())) {
            showPlayerName(player);
        } else {
            hidePlayerName(player);
        }
    }

    private void hidePlayerName(Player player) {
        hiddenNamesTeam.addEntry(player.getName());
        
        // Устанавливаем невидимый символ как отображаемое имя, если опция включена
        if (useInvisibleCharacter) {
            player.setDisplayName(invisibleCharacter);
            
            // Скрываем имя в табе, если опция включена
            if (hideInTabList) {
                player.setPlayerListName(invisibleCharacter);
            }
        }
    }
    
    /**
     * Переключает видимость ника игрока
     * @param player Игрок, для которого нужно переключить видимость ника
     * @return true, если ник стал видимым, false, если ник стал скрытым
     */
    public boolean togglePlayerNameVisibility(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        if (nameVisibilityStorage.isNameVisible(playerUUID)) {
            // Если ник был видимым, скрываем его
            nameVisibilityStorage.removeVisibleName(playerUUID);
            hidePlayerName(player);
            return false;
        } else {
            // Если ник был скрытым, делаем его видимым
            nameVisibilityStorage.addVisibleName(playerUUID);
            showPlayerName(player);
            return true;
        }
    }
    
    /**
     * Делает ник игрока видимым
     * @param player Игрок, чей ник нужно сделать видимым
     */
    private void showPlayerName(Player player) {
        if (hiddenNamesTeam.hasEntry(player.getName())) {
            hiddenNamesTeam.removeEntry(player.getName());
        }
        
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }
        Player clickedPlayer = (Player) event.getRightClicked();
        if (clickedPlayer.isInvisible()) {
            return;
        }

        Player clickingPlayer = event.getPlayer();
        showPlayerNameInActionbar(clickingPlayer, clickedPlayer);
    }

    private void showPlayerNameInActionbar(Player clickingPlayer, Player clickedPlayer) {
        String name = nameFormat;
        name = PlaceholderAPI.setPlaceholders(clickedPlayer, name);
        clickingPlayer.sendActionBar(name);
        new BukkitRunnable() {
            @Override
            public void run() {
                clickingPlayer.sendActionBar("");
            }
        }.runTaskLater(this, displayTime * 20L);
    }
}

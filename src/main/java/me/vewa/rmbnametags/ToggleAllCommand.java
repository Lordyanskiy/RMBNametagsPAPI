package me.vewa.rmbnametags;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleAllCommand implements CommandExecutor {
    private RMBNametags plugin;
    private boolean allNamesVisible = false;

    public ToggleAllCommand(RMBNametags plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        allNamesVisible = !allNamesVisible;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isVisible = plugin.togglePlayerNameVisibility(player);
            // Убеждаемся, что все игроки имеют одинаковое состояние видимости ника
            if (isVisible != allNamesVisible) {
                plugin.togglePlayerNameVisibility(player);
            }
        }
        
        if (allNamesVisible) {
            sender.sendMessage(plugin.getMessage("all-names-visible"));
        } else {
            sender.sendMessage(plugin.getMessage("all-names-hidden"));
        }
        
        return true;
    }
} 
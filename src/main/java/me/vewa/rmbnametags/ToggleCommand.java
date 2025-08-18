package me.vewa.rmbnametags;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import me.clip.placeholderapi.PlaceholderAPI;

public class ToggleCommand implements CommandExecutor {
    private RMBNametags plugin;

    public ToggleCommand(RMBNametags plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-not-found", "%player_name%", ""));
            return true;
        }

        Player targetPlayer;
        if (args.length == 0) {
            targetPlayer = (Player) sender;
        } else {
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getMessage("player-not-found", "%player_name%", args[0]));
                return true;
            }
        }

        boolean isVisible = plugin.togglePlayerNameVisibility(targetPlayer);
        
        if (isVisible) {
            sender.sendMessage(plugin.getMessage("name-visible", "%player_name%", targetPlayer.getName()));
        } else {
            sender.sendMessage(plugin.getMessage("name-hidden", "%player_name%", targetPlayer.getName()));
        }
        
        return true;
    }
} 
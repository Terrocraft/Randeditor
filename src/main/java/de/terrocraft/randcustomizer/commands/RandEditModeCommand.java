package de.terrocraft.randcustomizer.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import de.terrocraft.randcustomizer.RandCustomizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RandEditModeCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou are not a Player.");
            return true;
        }
        Player player = ((Player) sender);
        if (args.length == 1) {
            if (args[0].equals("set")) {
                if (!sender.hasPermission("randcustomizer.randeditmode.set")) {
                    player.sendMessage(RandCustomizer.noperm);
                    return true;
                }
                RandCustomizer.getInstance().saveItems(player.getInventory().getContents());
                player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("massage.adminmode.saved"));
                return true;
            }
        }
        if(RandCustomizer.getInstance().getInEditMode().contains(player.getUniqueId())) {
            RandCustomizer.getInstance().resetPlayer(player);
        } else {
            PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);
            Plot plot = plotPlayer.getCurrentPlot();

            if (plot == null) {
                player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("fehler.noplot"));
                return true;
            }

            if(!plot.isOwner(player.getUniqueId()) && !player.hasPermission("randcustomizer.randeditmode.bypass")) {
                player.sendMessage(RandCustomizer.noperm);
                return true;
            }
            RandCustomizer.setPlotForPlayer(player.getUniqueId(), plot);
            RandCustomizer.getInstance().putPlayer(player);
            player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("massage.editmode.active"));
        }
        return true;
    }


        @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player && sender.hasPermission("randcustomizer.randeditmode.set")) {
            return List.of("set");
        }
        return new ArrayList<>();
    }
}

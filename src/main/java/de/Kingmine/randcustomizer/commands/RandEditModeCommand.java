package de.Kingmine.randcustomizer.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import de.Kingmine.randcustomizer.KingMineRandCustomizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandEditModeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cDu musst ein Spieler sein, um diese Aktion auszuführen.");
            return true;
        }
        Player player = ((Player) sender);
        if(args.length == 1) {
            if(args[0].equals("set")) {
                if(!sender.hasPermission("randcustomizer.randeditmode.set")) {
                    player.sendMessage("§6Rand-Edit-Mode: §cKeine Berechtigung.");
                    return true;
                }
                KingMineRandCustomizer.getInstance().saveItems(player.getInventory().getContents());
                player.sendMessage("§6Rand-Edit-Mode: §aErfolgreich!");
                return true;
            }
        }
        if(KingMineRandCustomizer.getInstance().getInEditMode().contains(player.getUniqueId())) {
            KingMineRandCustomizer.getInstance().resetPlayer(player);
            player.sendMessage("§6Rand-Edit-Mode: §cinaktiv");
        } else {
            PlotPlayer plotPlayer = PlotPlayer.from(player);
            Plot plot = plotPlayer.getCurrentPlot();
            if(plot == null) {
                player.sendMessage("§6Rand-Edit-Mode: §cDu stehst nicht auf einem Plot.");
                return true;
            }
            if(!plot.isOwner(player.getUniqueId()) && !player.hasPermission("randcustomizer.randeditmode.bypass")) {
                player.sendMessage("§6Rand-Edit-Mode: §cKeine Berechtigung.");
                return true;
            }
            KingMineRandCustomizer.getInstance().putPlayer(player);
            player.sendMessage("§6Rand-Edit-Mode: §aaktiv");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && sender.hasPermission("randcustomizer.randeditmode.set")) {
            return Arrays.asList("set");
        }
        return new ArrayList<>();
    }
}

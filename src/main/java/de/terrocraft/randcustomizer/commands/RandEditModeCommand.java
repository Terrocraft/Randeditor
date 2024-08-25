package de.terrocraft.randcustomizer.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import de.terrocraft.randcustomizer.RandCustomizer;
import de.terrocraft.randcustomizer.util.PlotSquaredUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.terrocraft.randcustomizer.util.PlotSquaredUtil.isLocationInRange;

public class RandEditModeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cYou dont a Player.");
            return true;
        }
        Player player = ((Player) sender);
        if(args.length == 1) {
            if(args[0].equals("set")) {
                if(!sender.hasPermission("randcustomizer.randeditmode.set")) {
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
            player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("massage.editmode.inactive"));
        } else {
            PlotPlayer plotPlayer = PlotPlayer.from(player);
            Plot plot = plotPlayer.getCurrentPlot();

            com.plotsquared.core.location.Location[] corners = plot.getCorners();

            int minX = Arrays.stream(corners).mapToInt(com.plotsquared.core.location.Location::getX).min().orElseThrow();
            int minZ = Arrays.stream(corners).mapToInt(com.plotsquared.core.location.Location::getZ).min().orElseThrow();
            int maxX = Arrays.stream(corners).mapToInt(com.plotsquared.core.location.Location::getX).max().orElseThrow();
            int maxZ = Arrays.stream(corners).mapToInt(com.plotsquared.core.location.Location::getZ).max().orElseThrow();

            // Create locations for the minimum and maximum points, expanded by 1 block
            Location min = new Location(player.getWorld(), minX - 1, 0, minZ - 1);
            Location max = new Location(player.getWorld(), maxX + 1, player.getWorld().getMaxHeight(), maxZ + 1);

            Location playerLocation = player.getLocation();

            if (!PlotSquaredUtil.isLocationInRange(playerLocation, min, max)) {
                player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("fehler.noplot"));
                return true;
            }

            if(!plot.isOwner(player.getUniqueId()) && !player.hasPermission("randcustomizer.randeditmode.bypass")) {
                player.sendMessage(RandCustomizer.noperm);
                return true;
            }
            RandCustomizer.getInstance().putPlayer(player);
            player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("massage.editmode.active"));
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
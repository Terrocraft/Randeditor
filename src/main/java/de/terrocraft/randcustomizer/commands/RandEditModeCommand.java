package de.terrocraft.randcustomizer.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import de.terrocraft.randcustomizer.RandCustomizer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RandEditModeCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou are not a Player.");
            return true;
        }
        Player player = ((Player) sender);

        //-----------------------------SET-------------------------------------------------

        if (args.length == 1) {
            if (args[0].equals("save")) {
                if (!sender.hasPermission("randcustomizer.randeditmode.set")) {
                    player.sendMessage(RandCustomizer.noperm);
                    return true;
                }
                RandCustomizer.getInstance().saveItems(player.getInventory().getContents());
                player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.adminmode.saved"));
                return true;
            }
        }
        /*
        //-----------------------------------EDITMODE----------------------------------------
        if (RandCustomizer.getInstance().getInEditMode().contains(player.getUniqueId())) {
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

            player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.editmode.active"));
        }
        return true;
    }*/
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("edit")) {
                if (!player.hasPermission("randcustomizer.randeditmode.set")) {
                    player.sendMessage(RandCustomizer.noperm);
                    return true;
                }
                RandCustomizer.getInstance().putPlayer(player);
                giveSearchItem(player);
                return true;
            }
        }
        return true;
    }

    private void giveSearchItem(Player player) {
        ItemStack searchItem = new ItemStack(Material.PAPER);
        ItemMeta meta = searchItem.getItemMeta();
        meta.setDisplayName("§aSearch");
        searchItem.setItemMeta(meta);
        player.getInventory().setItem(0, searchItem);  // Setze das Such-Item in Slot 0
        player.sendMessage("§aYou can now search for items by typing their name in chat!");
    }


        //---------------------------------TABCOMPLETE---------------------------------------
        @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player && sender.hasPermission("randcustomizer.randeditmode.set")) {
            return List.of("edit");
        }
        return new ArrayList<>();
    }
}

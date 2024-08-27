package de.terrocraft.randcustomizer.commands;

import de.terrocraft.randcustomizer.RandCustomizer;
import de.terrocraft.randcustomizer.util.ItemBuilder;
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
        if (args.length == 0) {
                RandCustomizer.getInstance().putPlayer(player);
                giveSearchItem(player);
                giveMaterialItem(player);
                return true;
        }
        return true;
    }

    private void giveSearchItem(Player player) {
        ItemStack searchItem = new ItemBuilder().setMeterial(Material.PAPER).setTitle("§aSearch").build();
        player.getInventory().setItem(0, searchItem);
        player.sendMessage("§aYou can now search for items by typing their name in chat while holding the Search item!");
    }

    private void giveMaterialItem(Player player) {
        ItemStack materialItem = new ItemBuilder().setMeterial(Material.BARREL).setTitle("§aMaterials").build();
        player.getInventory().setItem(8, materialItem);
    }


        //---------------------------------TABCOMPLETE---------------------------------------
        @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        ArrayList<String> adminCommands = new ArrayList<>();
        if(sender instanceof Player && sender.hasPermission("randcustomizer.randeditmode.edit")) {
            adminCommands.add("edit");
            adminCommands.add("save");
            return adminCommands;
        }
        return new ArrayList<>();
    }
}

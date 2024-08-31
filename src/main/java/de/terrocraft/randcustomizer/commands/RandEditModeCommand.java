package de.terrocraft.randcustomizer.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import de.terrocraft.randcustomizer.RandCustomizer;
import de.terrocraft.randcustomizer.util.ItemBuilder;
import de.terrocraft.randcustomizer.util.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RandEditModeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender,Command command,String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou are not a Player.");
            return true;
        }
        Player player = ((Player) sender);

        //----------------------------EDITMODE-------------------------------------------------------------

        if (args.length == 0) {
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


                giveSearchItem(player);
                giveMaterialItem(player);
            }
        } else


        //-----------------------------SET MATERIALS FOR EDITMODE-------------------------------------------------

        if (args.length == 1) {
            if (args[0].equals("add")) {
                if (!sender.hasPermission("randcustomizer.randeditmode.set")) {
                    player.sendMessage(RandCustomizer.noperm);
                    return true;
                }
                ItemStack itemInHand = player.getItemInHand();
                if (itemInHand.getType() == Material.AIR) {
                    player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.adminmode.no-item-in-hand"));
                    return true;
                }

                if (RandCustomizer.materials.getList("materials", new ArrayList<ItemStack>()).contains(itemInHand)) {
                    player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.adminmode.item-already-in-edit-inventory").replace("%ITEM%", itemInHand.getType().toString()));
                    return true;
                }

                RandCustomizer.getInstance().addItem(itemInHand);
                player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.adminmode.added-item").replace("%ITEM%", itemInHand.getType().toString()));
                return true;
            } else if (args[0].equals("remove")) {
                if (!sender.hasPermission("randcustomizer.randeditmode.set")) {
                    player.sendMessage(RandCustomizer.noperm);
                    return true;
                }

                Utils.openAdminEditInventory(player);

            }
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
    public List<String> onTabComplete(CommandSender sender,Command command,String label, String[] args) {
        ArrayList<String> adminCommands = new ArrayList<>();
        if(sender instanceof Player && sender.hasPermission("randcustomizer.randeditmode.add")) {
            adminCommands.add("add");
            return adminCommands;
        }
        return new ArrayList<>();
    }
}

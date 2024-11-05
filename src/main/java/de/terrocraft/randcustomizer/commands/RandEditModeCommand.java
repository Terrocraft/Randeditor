package de.terrocraft.randcustomizer.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import de.terrocraft.randcustomizer.RandCustomizer;
import de.terrocraft.randcustomizer.util.ConfigUtil;
import de.terrocraft.randcustomizer.util.ItemBuilder;
import de.terrocraft.randcustomizer.util.SConfig;
import de.terrocraft.randcustomizer.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
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

                if (!player.hasPermission("randcustomizer.randeditmode")){
                    player.sendMessage(RandCustomizer.noperm);
                    return true;
                }

                if (RandCustomizer.config.getBoolean("sound-toggle-editmode")){
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 100, 2);
                }

                RandCustomizer.setPlotForPlayer(player.getUniqueId(), plot);
                RandCustomizer.getInstance().putPlayer(player);


                giveSearchItem(player);
                giveMaterialItem(player);
                giveBarrier(player);
            }
        } else


        //-----------------------------SET MATERIALS FOR EDITMODE-------------------------------------------------

        if (args.length == 1) {
            if (args[0].equals("add")) {
                if (!sender.hasPermission("randcustomizer.randeditmode.add")) {
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
                if (!sender.hasPermission("randcustomizer.randeditmode.remove")) {
                    player.sendMessage(RandCustomizer.noperm);
                    return true;
                }

                ItemStack itemInHand = player.getItemInHand();

                if (itemInHand.getType() == Material.AIR) {
                    player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.adminmode.air-remove-item"));
                    return true;
                }

                List<?> materials = RandCustomizer.materials.getList("materials");
                boolean itemFound = false;

                for (Object material : materials) {
                    if (material instanceof ItemStack) {
                        ItemStack listItem = (ItemStack) material;
                        if (listItem.getType() == itemInHand.getType()) {
                            RandCustomizer.getInstance().removeItem(listItem);
                            player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.adminmode.item-removed").replace("%ITEM%", itemInHand.getType().toString()));
                            itemFound = true;
                            break;
                        }
                    }
                }

                if (!itemFound) {
                    player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.adminmode.remove-item-not-exists"));
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("randcustomizer.randeditmode.reload")) {
                    sender.sendMessage(RandCustomizer.noperm);
                    return true;
                }

                ConfigUtil.cachemap.clear();

                if (!RandCustomizer.getInstance().getDataFolder().exists()) {
                    RandCustomizer.getInstance().getDataFolder().mkdirs();
                }

                Bukkit.getLogger().info("Loading configuration files...");

                RandCustomizer.config = new SConfig(new File(RandCustomizer.getInstance().getDataFolder(), "config.yml"), "config");
                RandCustomizer.materials = new SConfig(new File(RandCustomizer.getInstance().getDataFolder(), "materials.yml"), "materials");
                RandCustomizer.language = new SConfig(new File(RandCustomizer.getInstance().getDataFolder(), "language.yml"), "language");
                RandCustomizer.replaceMaterials = new SConfig(new File(RandCustomizer.getInstance().getDataFolder(), "replace-materials.yml"), "replace-materials");
                RandCustomizer.BlockPermissions = new SConfig(new File(RandCustomizer.getInstance().getDataFolder(), "BlockPermissions.yml"), "BlockPermissions");

                RandCustomizer.getInstance().setlanguage();
                RandCustomizer.getInstance().setConfig();
                RandCustomizer.getInstance().setReplaceMaterials();

                sender.sendMessage(RandCustomizer.prefix + "§aAll configs were reloaded.");
            }

        }


        return true;
    }

    private void giveSearchItem(Player player) {
        ItemStack searchItem = new ItemBuilder().setMeterial(Material.PAPER).setTitle("§aSearch").build();
        player.getInventory().setItem(0, searchItem);
        player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.editmode.search.giveitem"));
    }

    private void giveMaterialItem(Player player) {
        ItemStack materialItem = new ItemBuilder().setMeterial(Material.BARREL).setTitle("§aMaterials").build();
        player.getInventory().setItem(8, materialItem);
    }

    private void giveBarrier(Player player) {
        if (!RandCustomizer.config.getBoolean("Barrier-in-hotbar")){
            return;
        }
        ItemStack materialItem = new ItemBuilder().setMeterial(Material.BARRIER).setTitle("§4Air").build();
        player.getInventory().setItem(4, materialItem);
    }


        //---------------------------------TABCOMPLETE---------------------------------------
        @Override
    public List<String> onTabComplete(CommandSender sender,Command command,String label, String[] args) {
        ArrayList<String> adminCommands = new ArrayList<>();
        if (sender instanceof Player) {

            if (sender.hasPermission("randcustomizer.randeditmode.add")) adminCommands.add("add");

            if (sender.hasPermission("randcustomizer.randeditmode.remove")) adminCommands.add("remove");

            if (sender.hasPermission("randcustomizer.randeditmode.reload")) adminCommands.add("reload");

            return adminCommands;
        }
        return new ArrayList<>();
    }
}

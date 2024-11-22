package de.terrocraft.randcustomizer.listener;

import de.terrocraft.randcustomizer.RandCustomizer;
import de.terrocraft.randcustomizer.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class AdminGUIListener implements Listener {

    @EventHandler
    public void onAdminGUIClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        InventoryView view = e.getView();
        ItemStack clickedItem = e.getCurrentItem();

        // Check if the clicked item is valid
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Check if it's the Admin inventory
        if (view.getOriginalTitle().equals("§cAdmin§7-§aEdit§7-§eInventory")) {
            if (p.hasPermission("randcustomizer.randeditmode.set")) {
                Inventory clickedInventory = e.getClickedInventory();

                if (clickedInventory != null) {
                    if (clickedInventory.getSize() == 54) {
                        e.setCancelled(true);
                        clickedInventory.setItem(e.getSlot(), null);
                        p.updateInventory();

                    } else if (clickedInventory.getSize() == 36) {
                        if (clickedItem.getType() == Material.ARROW) {
                            String displayName = clickedItem.getItemMeta().getDisplayName();
                            if ("§7Back".equals(displayName)) {
                                e.setCancelled(true);
                            } else if ("§7Forward".equals(displayName)) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onAdminGUIClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        InventoryView view = e.getView();

        if (view.getOriginalTitle().equals("§cAdmin§7-§aEdit§7-§eInventory")) {
            ItemStack[] itemStacks = e.getInventory().getContents();
            List<ItemStack> itemList = Arrays.asList(itemStacks);
            RandCustomizer.getInstance().setInv(itemList);
            Utils.removeNavigationButtons(p);
        }

    }


}

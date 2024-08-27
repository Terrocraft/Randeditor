package de.terrocraft.randcustomizer.util;

import de.terrocraft.randcustomizer.RandCustomizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    @EventHandler
    public static void onInventoryOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInUse = event.getItem();
        if (isMaterialItem(itemInUse)) {
            event.setCancelled(true);
            openEditInventory(player);
        }
    }

    public static void openEditInventory(Player player) {
        Inventory editInv = Bukkit.createInventory(null, 54, "§aEdit§7-§eInventory");

        editInv.setContents(RandCustomizer.materials.getList("materials", new ArrayList<ItemStack>()).toArray(new ItemStack[0]));

        player.openInventory(editInv);

        addNavigationButtons(player);
    }

    public static ItemStack createNavigationButton(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void addNavigationButtons(Player player) {
        ItemStack backButton = createNavigationButton(Material.ARROW, "§7Back", "§7Gehe zur vorherigen Seite");
        ItemStack forwardButton = createNavigationButton(Material.ARROW, "§7Forward", "§7Gehe zur nächsten Seite");
        player.getInventory().setItem(9, backButton);
        player.getInventory().setItem(17, forwardButton);
    }

    public static void removeNavigationButtons(Player player) {
        player.getInventory().setItem(9, new ItemStack(Material.AIR));
        player.getInventory().setItem(17, new ItemStack(Material.AIR));
    }


    public static boolean isSearchItem(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && "§aSearch".equals(meta.getDisplayName());
    }

    public static boolean isMaterialItem(ItemStack item) {
        if (item == null || item.getType() != Material.BARREL) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && "§aMaterials".equals(meta.getDisplayName());
    }


    public static List<ItemStack> findMatchingItems(String searchTerm) {
        List<ItemStack> results = new ArrayList<>();
        searchTerm = searchTerm.toLowerCase();

        for (ItemStack material : RandCustomizer.materials.getList("materials", new ArrayList<ItemStack>()).toArray(new ItemStack[0])) {
            if (material == null) {
                continue;
            }

            if (material.toString().toLowerCase().contains(searchTerm)) {
                results.add(material);
            }
        }

        return results;
    }

}

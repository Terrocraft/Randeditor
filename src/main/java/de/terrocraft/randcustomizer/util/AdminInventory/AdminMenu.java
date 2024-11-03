package de.terrocraft.randcustomizer.util.AdminInventory;

import de.terrocraft.randcustomizer.RandCustomizer;
import de.terrocraft.randcustomizer.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AdminMenu {
    public static void OpenAdminMenu(Player p){
        if (!p.hasPermission("randcustomizer.admin.openmenu")){
            p.sendMessage(RandCustomizer.noperm);
            return;
        }

        Inventory searchInventory = Bukkit.createInventory(null,  27, "§c§lAdminMenu");

        ItemBuilder itemBuilder = new ItemBuilder();

        ItemStack BlackGlassPane = itemBuilder.setMeterial(Material.BLACK_STAINED_GLASS_PANE).setTitle(null).setLore(null).build();

        //ItemStack AddItemMenu = itemBuilder.setMeterial(Material.)



        for (int i = 0; i < 28; i++){
            searchInventory.setItem(i, BlackGlassPane);
        }


    }
}

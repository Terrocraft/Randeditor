package de.terrocraft.randcustomizer.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;

public class ItemBuilder {
    ItemStack item;
    ItemMeta itemMeta;

    public ItemBuilder setMeterial(Material material) {
        item = new ItemStack(material);
        itemMeta = item.getItemMeta();
        return this;
    }
    public ItemBuilder setCount(int Count) {
        item.setAmount(Count);
        return this;
    }
    public ItemBuilder setTitle(String title) {
        itemMeta.setDisplayName(title);
        return this;
    }
    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }
    public ItemBuilder setCustomModelData(int CustomModelData) {
        itemMeta.setCustomModelData(CustomModelData);
        return this;
    }
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(true);
        return this;
    }
    public ItemBuilder setEnchantments(HashMap<Enchantment, Integer> enchantments) {
        item.addEnchantments(enchantments);
        return this;
    }
    public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
        itemMeta.addItemFlags(itemFlags);
        return this;
    }
    public ItemBuilder setLeatherColor(Color color) {
        if (itemMeta instanceof LeatherArmorMeta){
            ((LeatherArmorMeta) itemMeta).setColor(color);
        }
        return this;
    }
    public ItemBuilder setBookPages(List<String> strings) {
        if (itemMeta instanceof BookMeta){
            ((BookMeta) itemMeta).setPages(strings);
        }
        return this;
    }
    public ItemBuilder setBookTitle(String title) {
        if (itemMeta instanceof BookMeta){
            ((BookMeta) itemMeta).setTitle(title);
        }
        return this;
    }
    public ItemBuilder setBookAuthor(String Author) {
        if (itemMeta instanceof BookMeta){
            ((BookMeta) itemMeta).setAuthor(Author);
        }
        return this;
    }
    public ItemBuilder setHeadPlayer(Player player) {
        if (itemMeta instanceof SkullMeta){
            ((SkullMeta) itemMeta).setOwningPlayer((OfflinePlayer) player);
        }
        return this;
    }
    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }
}
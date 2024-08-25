package de.terrocraft.randcustomizer;

import de.terrocraft.randcustomizer.commands.RandEditModeCommand;
import de.terrocraft.randcustomizer.listener.PlayerBlockListener;
import de.terrocraft.randcustomizer.util.ConfigUtil;
import de.terrocraft.randcustomizer.util.SConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class RandCustomizer extends JavaPlugin {
    private static RandCustomizer instance;

    private final List<UUID> inEditMode = new ArrayList<>();
    private final Map<UUID, ItemStack[]> playerInventory = new HashMap<>();
    private SConfig config;
    public static SConfig language;
    public static String prefix;
    public static String noperm;
    private SConfig replaceMaterials;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        config = ConfigUtil.getConfig("config");
        language = ConfigUtil.getConfig("language");
        replaceMaterials = ConfigUtil.getConfig("replace-materials");

        setlaguageconfig();
        if(!replaceMaterials.getFile().isFile()) {
            replaceMaterials.setDefault(Material.BARRIER.name(), Material.AIR.name());
            replaceMaterials.setDefault(Material.WATER_BUCKET.name(), Material.WATER.name());
            replaceMaterials.setDefault(Material.LAVA_BUCKET.name(), Material.LAVA.name());
        }

        getCommand("randeditmode").setExecutor(new RandEditModeCommand());

        getServer().getPluginManager().registerEvents(new PlayerBlockListener(), this);


        prefix = language.getString("prefix");
        noperm = language.getString("no-perm");
    }

    public static void setlaguageconfig(){
        if(!language.getFile().isFile()) {
            language.setDefault("prefix", "§6Rand-Edit-Mode: ");
            language.setDefault("no-perm", "§4You don't have permission to do that.");
            language.setDefault("fehler.noplot", "§4You are not standing on a Plot.");
            language.setDefault("fehler.other", "§4Error, send a massage to a Admin!");
            language.setDefault("massage.editmode.active", "§2Editmode Active!");
            language.setDefault("massage.editmode.inactive", "§4Editmode Inactive!");
            language.setDefault("massage.adminmode.saved", "§2Inventory Saved!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            resetPlayer(onlinePlayer);
        }
    }

    public List<UUID> getInEditMode() {
        return inEditMode;
    }

    public void resetPlayer(Player player) {
        while (RandCustomizer.getInstance().getInEditMode().remove(player.getUniqueId()));
        if(!playerInventory.containsKey(player.getUniqueId())) {
            return;
        }
        player.getInventory().setContents(playerInventory.get(player.getUniqueId()));
        playerInventory.remove(player.getUniqueId());
        player.saveData();
    }

    public void putPlayer(Player player) {
        player.closeInventory();
        RandCustomizer.getInstance().getInEditMode().add(player.getUniqueId());
        playerInventory.put(player.getUniqueId(), player.getInventory().getContents());
        try {
            player.getInventory().setContents(config.getList("items", new ArrayList<ItemStack>()).toArray(new ItemStack[0]));
        } catch (Throwable throwable) {
            resetPlayer(player);
            player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("fehler.other"));
            return;
        }
    }

    public void saveItems(ItemStack[] items) {
        config.set("items", Arrays.asList(items));
        config.save();
    }

    public Map<UUID, ItemStack[]> getPlayerInventory() {
        return playerInventory;
    }

    public SConfig getReplaceMaterials() {
        return replaceMaterials;
    }

    @Override
    public SConfig getConfig() {
        return config;
    }

    public static RandCustomizer getInstance() {
        return instance;
    }
}
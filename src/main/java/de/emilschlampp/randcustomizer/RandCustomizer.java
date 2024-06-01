package de.emilschlampp.randcustomizer;

import de.emilschlampp.randcustomizer.commands.RandEditModeCommand;
import de.emilschlampp.randcustomizer.listener.PlayerBlockListener;
import de.emilschlampp.randcustomizer.util.ConfigUtil;
import de.emilschlampp.randcustomizer.util.SConfig;
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
    private SConfig replaceMaterials;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        config = ConfigUtil.getConfig("config");
        replaceMaterials = ConfigUtil.getConfig("replace-materials");

        if(!replaceMaterials.getFile().isFile()) {
            replaceMaterials.setDefault(Material.BARRIER.name(), Material.AIR.name());
        }

        getCommand("randeditmode").setExecutor(new RandEditModeCommand());

        getServer().getPluginManager().registerEvents(new PlayerBlockListener(), this);



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
        RandCustomizer.getInstance().getInEditMode().add(player.getUniqueId());
        playerInventory.put(player.getUniqueId(), player.getInventory().getContents());
        try {
            player.getInventory().setContents(config.getList("items", new ArrayList<ItemStack>()).toArray(new ItemStack[0]));
        } catch (Throwable throwable) {
            resetPlayer(player);
            player.sendMessage("§cUpsi, das sollte so nicht passieren. Ein unerwarteter Fehler ist aufgetreten und der Vorgang wurde abgebrochen.");
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

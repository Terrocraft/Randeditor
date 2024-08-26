package de.terrocraft.randcustomizer;

import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import de.terrocraft.randcustomizer.commands.RandEditModeCommand;
import de.terrocraft.randcustomizer.listener.PlayerBlockListener;
import de.terrocraft.randcustomizer.util.ConfigUtil;
import de.terrocraft.randcustomizer.util.SConfig;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public final class RandCustomizer extends JavaPlugin {
    private static RandCustomizer instance;

    private final List<UUID> inEditMode = new ArrayList<>();
    private final Map<UUID, ItemStack[]> playerInventory = new HashMap<>();
    private static final Map<UUID, Plot> playerPlot = new HashMap<>();
    public static SConfig config;
    public static SConfig materials;
    public static SConfig language;
    public static String prefix;
    public static String noperm;
    public static SConfig BlockPermissions;
    private SConfig replaceMaterials;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        config = ConfigUtil.getConfig("config");
        materials = ConfigUtil.getConfig("materials");
        language = ConfigUtil.getConfig("language");
        replaceMaterials = ConfigUtil.getConfig("replace-materials");
        BlockPermissions = ConfigUtil.getConfig("BlockPermissions");

        setlanguage();
        setConfig();
        setReplaceMaterials();


        if(!BlockPermissions.getFile().isFile()) {
            BlockPermissions.setDefault("Blocks." + Material.COMMAND_BLOCK.name(), "randcustomizer.Block.Command_Block");
        }

        Objects.requireNonNull(getCommand("randeditmode")).setExecutor(new RandEditModeCommand());

        getServer().getPluginManager().registerEvents(new PlayerBlockListener(), this);

        prefix = language.getString("prefix");
      
        noperm = prefix + language.getString("no-perm");
        int pluginId = 23191;
        new Metrics(this, pluginId);

    }

    public void setlanguage(){
        if(!language.getFile().isFile()) {
            language.setDefault("prefix", "§6Rand-Edit-Mode: ");
            language.setDefault("no-perm", "§4You don't have permission to do that.");
            language.setDefault("noblock-perm", "§4You don't have permission to this Block.");
            language.setDefault("fehler.noplot", "§4You are not standing on a Plot.");
            language.setDefault("fehler.other", "§4Error, send a massage to a Admin!");
            language.setDefault("massage.editmode.active", "§2Editmode Active!");
            language.setDefault("massage.editmode.inactive", "§4Editmode Inactive!");
            language.setDefault("massage.adminmode.saved", "§2Inventory Saved!");
        }
    }



    public void setConfig(){
        if(!config.getFile().isFile()) {
            config.setDefault("radius-around-plot", 2);
            config.setDefault("road-edit-height-top", 0);
            config.setDefault("road-edit-height-bottom", 1);
        }
    }

    public void setReplaceMaterials() {
        if(!replaceMaterials.getFile().isFile()) {
            replaceMaterials.setDefault(Material.BARRIER.name(), Material.AIR.name());
            replaceMaterials.setDefault(Material.WATER_BUCKET.name(), Material.WATER.name());
            replaceMaterials.setDefault(Material.LAVA_BUCKET.name(), Material.LAVA.name());
        }
    }

    @Override
    public void onDisable() {
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
        playerPlot.remove(player.getUniqueId());
        player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("massage.editmode.inactive"));
    }

    public void putPlayer(Player player) {
        player.closeInventory();
        RandCustomizer.getInstance().getInEditMode().add(player.getUniqueId());
        playerInventory.put(player.getUniqueId(), player.getInventory().getContents());
        try {
            player.getInventory().setContents(materials.getList("materials", new ArrayList<ItemStack>()).toArray(new ItemStack[0]));
        } catch (Throwable throwable) {
            resetPlayer(player);
            player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("fehler.other"));
            return;
        }
    }

    public void saveItems(ItemStack[] items) {
        materials.set("materials", Arrays.asList(items));
        materials.save();
    }

    public static void setPlotForPlayer(UUID playerId, Plot plot) {
        playerPlot.put(playerId, plot);
    }

    public static Plot getPlotForPlayer(UUID playerId) {
        return playerPlot.get(playerId);
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
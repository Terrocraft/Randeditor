package de.terrocraft.randcustomizer;

import com.plotsquared.core.plot.Plot;
import de.terrocraft.randcustomizer.commands.RandEditModeCommand;
import de.terrocraft.randcustomizer.listener.AdminGUIListener;
import de.terrocraft.randcustomizer.listener.PlayerBlockListener;
import de.terrocraft.randcustomizer.listener.RandEditModeListener;
import de.terrocraft.randcustomizer.util.ConfigUtil;
import de.terrocraft.randcustomizer.util.SConfig;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class RandCustomizer extends JavaPlugin {

    private static RandCustomizer instance;

    private final List<UUID> inEditMode = new ArrayList<>();
    private final Map<UUID, ItemStack[]> playerInventory = new HashMap<>();
    private final Map<UUID, ItemStack[]> adminInventory = new HashMap<>();
    private static final Map<UUID, Plot> playerPlot = new HashMap<>();
    private static final Map<UUID, Boolean> playerFly = new HashMap<>();
    public static SConfig config;
    public static SConfig materials;
    public static SConfig language;
    public static String prefix;
    public static String noperm;
    public static SConfig BlockPermissions;
    public static SConfig replaceMaterials;

    private Map<UUID, Integer> playerPages = new HashMap<>();

    public int getCurrentPage(UUID playerId) {
        return playerPages.getOrDefault(playerId, 1);
    }

    public void setCurrentPage(UUID playerId, int page) {
        playerPages.put(playerId, page);
    }


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

        //Register Configs

        setlanguage();
        setConfig();
        setReplaceMaterials();

        if(!BlockPermissions.getFile().isFile()) {
            BlockPermissions.setDefault("Blocks." + Material.COMMAND_BLOCK.name(), "randcustomizer.Block.Command_Block");
        }

        Objects.requireNonNull(getCommand("randeditmode")).setExecutor(new RandEditModeCommand());

        getServer().getPluginManager().registerEvents(new PlayerBlockListener(), this);
        getServer().getPluginManager().registerEvents(new RandEditModeListener(this), this);
        getServer().getPluginManager().registerEvents(new AdminGUIListener(), this);
        prefix = language.getString("prefix");
      
        noperm = prefix + language.getString("no-perm");
        int pluginId = 23798;
        new Metrics(this, pluginId);

        registerBlockPermissions();
    }

    private void registerBlockPermissions() {
        for (String key : BlockPermissions.getConfigurationSection("Blocks").getKeys(false)) {
            String permissionString = BlockPermissions.getString("Blocks." + key);
            if (permissionString != null && !permissionString.isEmpty()) {
                Permission permission = new Permission(permissionString);
                Bukkit.getPluginManager().addPermission(permission);
            }
        }
    }

    public void setlanguage(){
        if(!language.getFile().isFile()) {
            language.setDefault("prefix", "§6Rand-Edit-Mode: ");
            language.setDefault("no-perm", "§4You don't have permission to do that.");
            language.setDefault("noblock-perm", "§4You don't have permission to this Block.");
            language.setDefault("fehler.noplot", "§4You are not standing on a Plot.");
            language.setDefault("fehler.other", "§4Error, send a message to a Admin!");
            language.setDefault("message.editmode.active", "§2Editmode Active!");
            language.setDefault("message.editmode.inactive", "§4Editmode Inactive!");
            language.setDefault("message.editmode.search.giveitem", "§aYou can now search for items by typing their name in chat while holding the Search item!");
            language.setDefault("message.editmode.search.openinv", "§aOpening search inventory...");
            language.setDefault("message.editmode.search.no-matching-items", "§cNo matching items found.");
            language.setDefault("message.adminmode.no-item-in-hand", "§cYou need to have an item on your cursor!");
            language.setDefault("message.adminmode.added-item", "§2%ITEM% was added to Edit-Inventory!");
            language.setDefault("message.adminmode.air-remove-item", "§cYou can not remove air!");
            language.setDefault("message.adminmode.remove-item-not-exists", "§cThis item is not in the material list!");
            language.setDefault("message.adminmode.item-removed", "§aThe item %ITEM% will be removed!");
            language.setDefault("message.adminmode.item-already-in-edit-inventory", "§c%ITEM% is already in the edit-inventory");
            language.setDefault("message.edit-inv.item-already-in-hotbar", "§c%ITEM% is already in your hotbar!");
            language.setDefault("message.edit-inv.item-added-to-hotbar", "§a%ITEM% added to your Hotbar!");
            language.setDefault("message.edit-inv.hotbar-is-full", "§cHotbar is full, cannot add item.");
            language.setDefault("message.edit-inv.no-more-items-to-display", "§cNo more items to display on this page.");
        }
    }

    public void setConfig(){
        if(!config.getFile().isFile()) {
            config.setDefault("radius-around-plot", 2);
            config.setDefault("road-edit-height-top", 2);
            config.setDefault("road-edit-height-bottom", 5);
            config.setDefault("fly-in-editmode", false);
            config.setDefault("sound-toggle-editmode", true);
            config.setDefault("Barrier-in-hotbar", true);
            config.setDefault("Deny-Bedrock-Break", true);
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

    public void saveAdminInventory(Player player) {
        adminInventory.put(player.getUniqueId(), player.getInventory().getContents());
        player.sendMessage(prefix + "message.admin.inventorysaved");
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
        player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.editmode.inactive"));
        if (playerFly.containsKey(player.getUniqueId())) {
            if (playerFly.get(player.getUniqueId())) {
                player.setFlying(true);
                player.setAllowFlight(true);
                return;
            }
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

    public void putPlayer(Player player) {
        player.closeInventory();
        RandCustomizer.getInstance().getInEditMode().add(player.getUniqueId());
        playerInventory.put(player.getUniqueId(), player.getInventory().getContents());
        player.getInventory().clear();

        if (config.getBoolean("fly-in-editmode")) {
            playerFly.put(player.getUniqueId(), player.getAllowFlight());
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    public void addItem(ItemStack item) {
        List<ItemStack> items = new ArrayList<>();
        items = (List<ItemStack>) RandCustomizer.materials.getList("materials", items);
        items.add(item);
        materials.set("materials", items);
        materials.save();
    }

    public void removeItem(ItemStack item) {
        List<ItemStack> items = new ArrayList<>();
        items = (List<ItemStack>) RandCustomizer.materials.getList("materials", items);
        items.remove(item);
        materials.set("materials", items);
        materials.save();
    }

    public void setInv(List<ItemStack> itemList) {
        materials.set("materials", itemList);
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
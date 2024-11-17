package de.terrocraft.randcustomizer.listener;

import de.terrocraft.randcustomizer.RandCustomizer;
import de.terrocraft.randcustomizer.util.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class RandEditModeListener implements Listener {

    private final RandCustomizer plugin;

    public RandEditModeListener(RandCustomizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (plugin.getInEditMode().contains(player.getUniqueId())) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (Utils.isSearchItem(itemInHand)) {
                event.setCancelled(true);
                String searchTerm = event.getMessage();

                List<ItemStack> searchResults = Utils.findMatchingItems(searchTerm);

                player.sendMessage("§aFound " + searchResults.size() + " matching items.");

                if (!searchResults.isEmpty()) {
                    Utils.openSearchInventory(player, searchResults, plugin);
                    player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.editmode.search.openinv"));
                } else {
                    player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.editmode.search.no-matching-items"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String command = e.getMessage().toLowerCase();

        if (RandCustomizer.getInstance().getInEditMode().contains(p.getUniqueId())) {
            List<String> blockedCommands = RandCustomizer.config.getStringList("Blocked-Commands");

            for (String blockedCommand : blockedCommands) {
                if (command.startsWith("/" + blockedCommand.toLowerCase())) {
                    e.setCancelled(true);
                    p.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.editmode.on-blocked-command").replace("%COMMAND%", blockedCommand));
                    return;
                }
            }
        }
    }


    @EventHandler
    public void OnPlayerDamage(EntityDamageEvent e){
        if (e.getEntity().getType().equals(EntityType.PLAYER)){
            Player p = (Player) e.getEntity();
            if (RandCustomizer.getInstance().getInEditMode().contains(p.getUniqueId())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void OnPlayerDeath(EntityDeathEvent e){
        if (e.getEntity().getType().equals(EntityType.PLAYER)){
            Player p = (Player) e.getEntity();
            if (RandCustomizer.getInstance().getInEditMode().contains(p.getUniqueId())){
                e.getDrops().clear();
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (plugin.getInEditMode().contains(p.getUniqueId())) {
            if (Utils.isMaterialItem(e.getItem())) {
                Utils.openEditInventory(p, 1);
            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String title = event.getView().getTitle();

        if (title.equals("§aEdit§7-§eInventory")) {
            event.setCancelled(true);

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                if (clickedItem.getType() == Material.ARROW) {
                    String displayName = clickedItem.getItemMeta().getDisplayName();
                    int currentPage = plugin.getCurrentPage(player.getUniqueId());

                    if ("§7Back".equals(displayName)) {
                        if (currentPage > 1) {
                            plugin.setCurrentPage(player.getUniqueId(), currentPage - 1);
                            Utils.openEditInventory(player, currentPage - 1);
                            Utils.addNavigationButtons(player);
                            player.playSound(player, Sound.BLOCK_LAVA_POP, 1.5f, 1);
                        }
                    } else if ("§7Forward".equals(displayName)) {
                        plugin.setCurrentPage(player.getUniqueId(), currentPage + 1);
                        Utils.openEditInventory(player, currentPage + 1);
                        Utils.addNavigationButtons(player);
                        player.playSound(player, Sound.BLOCK_LAVA_POP, 1.5f, 1);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (e.getView().getOriginalTitle().equals("§aEdit§7-§eInventory")) {
            Utils.removeNavigationButtons(p);
        }

    }


    @EventHandler
    public void onInventoryClickPlayerClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getOriginalTitle().equals("§aEdit§7-§eInventory")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                if (!(clickedItem.getType() == Material.ARROW)) {
                    int amount = clickedItem.getAmount();
                    int hotbarSlot = -1;

                    for (int i = 0; i < 9; i++) {
                        if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) {
                            hotbarSlot = i;
                            break;
                        }
                    }

                    if (hotbarSlot != -1) {
                        if (player.getInventory().contains(clickedItem.getType())) {
                            player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.edit-inv.item-already-in-hotbar").replace("%ITEM%", clickedItem.getType().toString()));
                            return;
                        }
                        player.getInventory().setItem(hotbarSlot, new ItemStack(clickedItem.getType(), amount));
                        player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.edit-inv.item-added-to-hotbar").replace("%ITEM%", clickedItem.getType().toString()));

                    } else {
                        player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("message.edit-inv.hotbar-is-full"));
                    }
                }
            }
        }
    }
}

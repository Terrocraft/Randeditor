package de.terrocraft.randcustomizer.listener;

import de.terrocraft.randcustomizer.RandCustomizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            if (isSearchItem(itemInHand)) {
                event.setCancelled(true);  // Chat-Nachricht unterdrücken
                String searchTerm = event.getMessage();

                List<ItemStack> searchResults = findMatchingItems(searchTerm);

                // Debug-Ausgabe
                player.sendMessage("§aFound " + searchResults.size() + " matching items.");

                if (!searchResults.isEmpty()) {
                    openSearchInventory(player, searchResults);
                    player.sendMessage("§aOpening search inventory...");
                } else {
                    player.sendMessage("§cNo matching items found.");
                }
            }
        }
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Search Results")) {
            event.setCancelled(true);  // Verhindert das Standardverhalten

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                int amount = clickedItem.getAmount();
                int hotbarSlot = -1;

                for (int i = 0; i < 9; i++) {
                    if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) {
                        hotbarSlot = i;
                        break;
                    }
                }

                if (hotbarSlot != -1) {
                    player.getInventory().setItem(hotbarSlot, new ItemStack(clickedItem.getType(), amount));
                    player.sendMessage("§aItem added to your Hotbar!");

                    player.closeInventory();
                } else {
                    player.sendMessage("§cHotbar is full, cannot add item.");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (e.getView().getOriginalTitle().equals("§aEdit§7-§eInventory")) {
            removeNavigationButtons(p);
        }

    }



    private boolean isSearchItem(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && "§aSearch".equals(meta.getDisplayName());
    }

    private boolean isMaterialItem(ItemStack item) {
        if (item == null || item.getType() != Material.BARREL) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && "§aMaterials".equals(meta.getDisplayName());
    }



    private List<ItemStack> findMatchingItems(String searchTerm) {
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

    @EventHandler
    public void onInventoryOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInUse = event.getItem();
        if (isMaterialItem(itemInUse)) {
            event.setCancelled(true);
            openEditInventory(player);
        }
    }

    private void openEditInventory(Player player) {
        Inventory editInv = Bukkit.createInventory(null, 54, "§aEdit§7-§eInventory");

        editInv.setContents(RandCustomizer.materials.getList("materials", new ArrayList<ItemStack>()).toArray(new ItemStack[0]));

        player.openInventory(editInv);

        addNavigationButtons(player);
    }

    private void addNavigationButtons(Player player) {
        ItemStack backButton = createNavigationButton(Material.ARROW, "§7Back", "§7Gehe zur vorherigen Seite");
        ItemStack forwardButton = createNavigationButton(Material.ARROW, "§7Forward", "§7Gehe zur nächsten Seite");
        player.getInventory().setItem(9, backButton);
        player.getInventory().setItem(17, forwardButton);
    }

    private void removeNavigationButtons(Player player) {
        player.getInventory().setItem(9, new ItemStack(Material.AIR));
        player.getInventory().setItem(17, new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onInventoryClickPlayerClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        // Prüfen, ob die Buttons im Spieler-Inventar geklickt wurden
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
                            player.sendMessage("§c" + clickedItem.getType() + " is already in your hotbar!");
                            return;
                        }
                        player.getInventory().setItem(hotbarSlot, new ItemStack(clickedItem.getType(), amount));
                        player.sendMessage("§aItem added to your Hotbar!");

                        player.closeInventory();

                    } else {
                        player.sendMessage("§cHotbar is full, cannot add item.");
                    }
                }
                if (clickedItem.getType() == Material.ARROW) {
                    String displayName = clickedItem.getItemMeta().getDisplayName();
                    if ("§7Back".equals(displayName)) {
                        // Handling für "Back"-Button
                        player.sendMessage("§cBack Button geklickt!");
                    } else if ("§7Forward".equals(displayName)) {
                        // Handling für "Forward"-Button
                        player.sendMessage("§aForward Button geklickt!");
                    }
                }
            }
        }
    }

    private ItemStack createNavigationButton(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    private void openSearchInventory(Player player, List<ItemStack> searchResults) {
        Inventory searchInventory = Bukkit.createInventory(null, 54, "Search Results");

        int index = 0;
        for (ItemStack item : searchResults) {
            if (index >= 54) break;  // Limit to 54 items (1 inventory)
            searchInventory.setItem(index, item);
            index++;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.openInventory(searchInventory);
            player.sendMessage("§aInventory should now be open.");
        });


    }


}

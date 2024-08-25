package de.terrocraft.randcustomizer.listener;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import de.terrocraft.randcustomizer.RandCustomizer;
import de.terrocraft.randcustomizer.util.ConverterUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

public class PlayerBlockListener implements Listener {
    public static final BlockFace[] CHECK = new BlockFace[]{
            BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST,
            BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST
    };

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getTo() == null) {
            return;
        }
        if(event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }
        if(!RandCustomizer.getInstance().getInEditMode().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        Player player = event.getPlayer();
        PlotPlayer plotPlayer = PlotPlayer.from(player);
        Plot plot = plotPlayer.getCurrentPlot();
        if(plot == null) {
            RandCustomizer.getInstance().resetPlayer(player);
            return;
        }
        if(!plot.isOwner(player.getUniqueId()) && !player.hasPermission("randcustomizer.randeditmode.bypass")) {
            RandCustomizer.getInstance().resetPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        onPlayerMove(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!RandCustomizer.getInstance().getInEditMode().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        if(event.getClickedBlock() == null) {
            return;
        }
        if(event.getItem() == null) {
            return;
        }
        Player player = event.getPlayer();
        Material material = Material.AIR;
        material = event.getItem().getType();

        if(RandCustomizer.getInstance().getReplaceMaterials().contains(material.name())) {
            try {
                material = Material.valueOf(RandCustomizer.getInstance().getReplaceMaterials().getString(material.name()));
            } catch (Throwable throwable) {
                player.sendMessage(RandCustomizer.prefix + RandCustomizer.language.getString("fehler.other"));
                return;
            }
        }

        event.setCancelled(true);

        if(!material.isBlock()) {
            return;
        }

        Block clicked = event.getClickedBlock();

        if(player.isSneaking()) {
            clicked = clicked.getRelative(event.getBlockFace());
        }

        com.plotsquared.core.location.Location plocClicked = ConverterUtil.toPlotsquaredLocation(clicked.getLocation());
        PlotArea areaClicked = PlotSquared.get().getPlotAreaManager().getPlotArea(plocClicked);
        if(areaClicked == null) {
            return;
        }
        if(areaClicked.getPlot(plocClicked) != null) {
            return;
        }

        boolean can = false;
        for (BlockFace face : CHECK) {
            Location location = clicked.getRelative(face).getLocation();
            com.plotsquared.core.location.Location ploc = ConverterUtil.toPlotsquaredLocation(location);
            PlotArea area = PlotSquared.get().getPlotAreaManager().getPlotArea(ploc);
            if(area == null) {
                continue;
            }
            Plot plot = area.getPlot(ploc);
            if(plot == null) {
                continue;
            }
            if(plot.isOwner(player.getUniqueId())) {
                can = true;
            }
        }

        if(!can) {
            return;
        }
        String searchConfig = "worlds."+areaClicked.getWorldName()+".wall.height";
        if(PlotSquared.get().getWorldConfiguration().isInt(searchConfig)) {
            int roadHeight = PlotSquared.get().getWorldConfiguration().getInt(searchConfig);
            if(clicked.getY() < roadHeight || clicked.getY() > roadHeight+1) {
                return;
            }
        }

        clicked.setType(material, false);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().equals(event.getWhoClicked().getInventory())) {
            return;
        }
        if(!RandCustomizer.getInstance().getInEditMode().contains(event.getWhoClicked().getUniqueId())) {
            return;
        }
        if(event.getClickedInventory() != null) {
            if(event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(event.getInventory().equals(event.getPlayer().getInventory())) {
            return;
        }
        if(!RandCustomizer.getInstance().getInEditMode().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!RandCustomizer.getInstance().getInEditMode().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        RandCustomizer.getInstance().resetPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(!RandCustomizer.getInstance().getInEditMode().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        if(!RandCustomizer.getInstance().getInEditMode().contains(event.getEntity().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }
}
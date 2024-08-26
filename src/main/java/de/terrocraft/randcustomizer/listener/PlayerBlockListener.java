package de.terrocraft.randcustomizer.listener;

import com.plotsquared.core.PlotSquared;
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

import java.util.Objects;

public class PlayerBlockListener implements Listener {
    public static final BlockFace[] CHECK = new BlockFace[]{
            BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST,
            BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST
    };

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null
                || event.getFrom().getBlock().equals(event.getTo().getBlock())
                || !RandCustomizer.getInstance().getInEditMode().contains(event.getPlayer().getUniqueId())) {
            return;
        }

        Player player = event.getPlayer();
        Plot plot = RandCustomizer.getPlotForPlayer(player.getUniqueId());

        if (plot == null || plot.getCorners() == null) {
            return;
        }

        com.plotsquared.core.location.Location[] corners = plot.getCorners();
        if (corners.length == 0) {
            return;
        }

        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (com.plotsquared.core.location.Location corner : corners) {
            int x = corner.getX();
            int z = corner.getZ();
            if (x < minX) minX = x;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (z > maxZ) maxZ = z;
        }

        int radius = RandCustomizer.config.getInt("radius-around-plot") - 1;

        minX -= radius;
        maxX += radius;
        minZ -= radius;
        maxZ += radius;

        Location playerLocation = player.getLocation();
        int playerX = playerLocation.getBlockX();
        int playerZ = playerLocation.getBlockZ();

        if (playerX < minX || playerX > maxX || playerZ < minZ || playerZ > maxZ) {
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

        if (RandCustomizer.BlockPermissions.contains("Block." + material.name())){
           String permission = RandCustomizer.BlockPermissions.getString("Block." + material.name());
            assert permission != null;
            if (!player.hasPermission(permission)){
                player.sendMessage(Objects.requireNonNull(RandCustomizer.language.getString("noblock-perm")));
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
        String searchConfig = "worlds." + areaClicked.getWorldName() + ".wall.height";
        int roadHeight = 0;

        if (PlotSquared.get().getWorldConfiguration().isInt(searchConfig)) {
            roadHeight = PlotSquared.get().getWorldConfiguration().getInt(searchConfig);
        } else {
            RandCustomizer.getInstance().getLogger().warning("No valid road height found for configuration path: " + searchConfig);
            return;
        }

        int roadEditHeightBottom = RandCustomizer.config.getInt("road-edit-height-bottom");
        int roadEditHeightTop = RandCustomizer.config.getInt("road-edit-height-top");
        int y = clicked.getY();

        if (y < roadHeight - roadEditHeightBottom || y > roadHeight + roadEditHeightTop) {
            return;
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

        if (event.getPlayer().hasPermission("randcustomizer.bypass")) {
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
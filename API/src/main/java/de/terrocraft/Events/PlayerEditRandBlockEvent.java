package de.terrocraft.Events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEditRandBlockEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled;
    private Player player;
    private Block ouldblock;
    private Material blockmaterial;

    public PlayerEditRandBlockEvent(Player player, Block ouldblock, Material blockmaterial) {
        this.player = player;
        this.blockmaterial = blockmaterial;
        this.ouldblock = ouldblock;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getPlayer(){
        return this.player;
    }

    public Block getouldblock(){
        return this.ouldblock;
    }

    public Material getBlockMaterial(){
        return this.blockmaterial;
    }

    public void setNewBlockMaterial(Material newblockmaterial){
        this.blockmaterial = newblockmaterial;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}

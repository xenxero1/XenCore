package me.neoblade298.neocore.bukkit.inventories;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.PlayerInventory;

import me.neoblade298.neocore.bukkit.listeners.InventoryListener;

public abstract class CorePlayerInventory {
	protected boolean handlesEvents, handlesMultiInvEvents; // If false, this inventory ignores events if another core inventory is in view
	protected Player p;
	protected PlayerInventory inv;
	public abstract void handleInventoryClick(InventoryClickEvent e);
	public abstract void handleInventoryDrag(InventoryDragEvent e);
	public abstract void handleInventoryClose(InventoryCloseEvent e);
	public CorePlayerInventory(Player p) {
		this(p, true, false);
	}
	public CorePlayerInventory(Player p, boolean handlesEvents) {
		this(p, handlesEvents, false);
	}
	public CorePlayerInventory(Player p, boolean handlesEvents, boolean handlesMultiInvEvents) {
		this.p = p;
		this.inv = p.getInventory();
		this.handlesEvents = handlesEvents;
		this.handlesMultiInvEvents = handlesMultiInvEvents;
		InventoryListener.registerPlayerInventory(p, this);
	}
	
	public boolean handlesEvents() {
		return handlesEvents;
	}
	
	// If true, handling for drag events is taken over by this inventory
	public boolean handlesMultiInvEvents() {
		return handlesMultiInvEvents;
	}
	
	public Player getPlayer() {
		return p;
	}
}

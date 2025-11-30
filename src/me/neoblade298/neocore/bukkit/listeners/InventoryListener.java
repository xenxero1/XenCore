package me.neoblade298.neocore.bukkit.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import me.neoblade298.neocore.bukkit.inventories.CoreInventory;
import me.neoblade298.neocore.bukkit.inventories.CorePlayerInventory;

public class InventoryListener implements Listener {
	private static HashMap<Player, CoreInventory> upperInvs = new HashMap<Player, CoreInventory>();
	private static HashMap<Player, CorePlayerInventory> lowerInvs = new HashMap<Player, CorePlayerInventory>();
	
	public static void registerInventory(Player p, CoreInventory inv) {
		if (inv == null) upperInvs.remove(p);
		upperInvs.put(p, inv);
	}
	public static void registerPlayerInventory(Player p, CorePlayerInventory inv) {
		if (inv == null) lowerInvs.remove(p);
		lowerInvs.put(p, inv);
	}
	
	public static void unregisterInventory(Player p) {
		upperInvs.remove(p);
	}
	
	public static void unregisterPlayerInventory(Player p) {
		lowerInvs.remove(p);
	}
	
	public static CoreInventory getUpperInventory(Player p) {
		return upperInvs.get(p);
	}
	
	public static CorePlayerInventory getLowerInventory(Player p) {
		return lowerInvs.get(p);
	}
	
	public static boolean hasOpenCoreInventory(Player p) {
		return lowerInvs.containsKey(p) || upperInvs.containsKey(p);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (lowerInvs.containsKey(p)) {
			CorePlayerInventory inv = lowerInvs.get(p);
			if (inv.handlesEvents() && e.getClickedInventory() == p.getInventory() || e.getClickedInventory() == null) {
				lowerInvs.get(p).handleInventoryClick(e);
				return;
			}
		}
		
		if (upperInvs.containsKey(p)) {
			upperInvs.get(p).handleInventoryClick(e);
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (lowerInvs.containsKey(p)) {
			CorePlayerInventory inv = lowerInvs.get(p);
			if (inv.handlesMultiInvEvents()) {
				lowerInvs.get(p).handleInventoryDrag(e);
				return;
			}
		}
		
		if (upperInvs.containsKey(p)) {
			upperInvs.get(p).handleInventoryDrag(e);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		CoreInventory upper = upperInvs.remove(p);
		if (upper != null) upper.handleInventoryClose(e);
		CorePlayerInventory lower = lowerInvs.remove(p);
		if (lower != null) lower.handleInventoryClose(e);
	}
}

package me.neoblade298.neocore.bukkit.inventories;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.neoblade298.neocore.bukkit.listeners.InventoryListener;
import me.neoblade298.neocore.bukkit.util.SkullUtil;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public abstract class CoreInventory {
	protected Inventory inv;
	protected Player p;
	public abstract void handleInventoryClick(InventoryClickEvent e);
	public abstract void handleInventoryDrag(InventoryDragEvent e);
	public abstract void handleInventoryClose(InventoryCloseEvent e);
	public CoreInventory(Player p, Inventory inv) {
		this.inv = inv;
		this.p = p;
		openInventory();
	}
	public Inventory getInventory() {
		return inv;
	}
	public Player getPlayer() {
		return p;
	}
	
	public static ItemStack createButton(String b64, TextComponent name, TextComponent... lore) {
		ItemStack item = SkullUtil.fromBase64(b64);
		return createButton(item, name, lore);
	}
	
	public static ItemStack createButton(Material mat, TextComponent name, TextComponent... lore) {
		ItemStack item = new ItemStack(mat);
		return createButton(item, name, lore);
	}
	
	public static ItemStack createButton(Material mat, TextComponent name, String lore, int pixelsPerLine, TextColor color) {
		ItemStack item = new ItemStack(mat);
		return createButton(item, name, Component.text(lore), pixelsPerLine, color);
	}
	
	public static ItemStack createButton(Material mat, TextComponent name, TextComponent lore, int pixelsPerLine, TextColor color) {
		ItemStack item = new ItemStack(mat);
		return createButton(item, name, lore, pixelsPerLine, color);
	}
	
	public static ItemStack createButton(ItemStack item, TextComponent name, TextComponent... lore) {
		ItemMeta meta = item.getItemMeta();
		meta.displayName(name.decorationIfAbsent(TextDecoration.ITALIC, State.FALSE));
		ArrayList<Component> list = new ArrayList<Component>();
		for (Component line : lore) {
			list.add(line.decorationIfAbsent(TextDecoration.ITALIC, State.FALSE));
		}
		meta.lore(list);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createButton(ItemStack item, TextComponent name, TextComponent lore, int pixelsPerLine, TextColor color) {
		ArrayList<TextComponent> list = SharedUtil.addLineBreaks(lore.colorIfAbsent(color), pixelsPerLine);
		TextComponent[] arr = new TextComponent[list.size()];
		int i = 0;
		for (TextComponent tc : list) {
			arr[i++] = tc;
		}
		return createButton(item, name, arr);
	}
	
	public void openInventory() {
		CorePlayerInventory lower = InventoryListener.getLowerInventory(p);
		p.openInventory(inv);
		InventoryListener.registerInventory(p, this);
		if (lower != null) InventoryListener.registerPlayerInventory(p, lower);
	}
}

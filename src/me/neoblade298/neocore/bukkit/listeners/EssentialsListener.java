package me.neoblade298.neocore.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import net.ess3.api.events.AfkStatusChangeEvent;

public class EssentialsListener implements Listener {
	@EventHandler
	public void onAfkChange(AfkStatusChangeEvent e) {
		BungeeAPI.sendPluginMessage("neocore-afk", new String[] {e.getAffected().getName(), e.getValue() ? (String) "T" : "F"});
	}
}

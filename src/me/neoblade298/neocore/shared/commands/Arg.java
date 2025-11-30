package me.neoblade298.neocore.shared.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.commands.BukkitTabResolver;
import me.neoblade298.neocore.bungee.commands.BungeeTabResolver;

public class Arg {
	private boolean required;
	private String display;
	private List<String> tabOptions;
	private BukkitTabResolver bukkitResolver;
	private BungeeTabResolver bungeeResolver;
	private ArgType type;
	
	public Arg(String display) {
		this(display, true, ArgType.OPTIONS);
	}
	
	public Arg(String display, boolean required) {
		this(display, required, ArgType.OPTIONS);
	}
	
	public Arg(String display, boolean required, ArgType type) {
		this.display = display;
		this.required = required;
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public String getDisplay() {
		return display;
	}
	
	public Arg setTabOptions(List<String> tabOptions) {
		this.tabOptions = tabOptions;
		Collections.sort(this.tabOptions);
		return this;
	}
	
	public List<String> getTabOptions() {
		return tabOptions;
	}
	
	public List<String> getTabOptions(Player p) {
		if (bukkitResolver != null) {
			return bukkitResolver.resolve(p);
		}
		return tabOptions;
	}
	
	public List<String> getTabOptions(com.velocitypowered.api.proxy.Player p) {
		if (bungeeResolver != null) {
			return bungeeResolver.resolve(p);
		}
		return tabOptions;
	}
	
	public ArgType getType() {
		return type;
	}
}

package me.neoblade298.neocore.bukkit.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.shared.commands.AbstractSubcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public abstract class Subcommand extends AbstractSubcommand {
	boolean overridesTab = false;
	public Subcommand(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	public abstract void run(CommandSender s, String[] args);
	// Must override getTabOptions for this to actually work
	public void overrideTabHandler() {
		overridesTab = true;
		enableTabComplete();
	}
	public List<String> getTabOptions(CommandSender s, String[] args) { return null; }
}

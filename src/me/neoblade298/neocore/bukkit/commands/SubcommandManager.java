package me.neoblade298.neocore.bukkit.commands;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.neoblade298.neocore.shared.commands.AbstractSubcommandManager;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.CommandArguments;
import me.neoblade298.neocore.shared.commands.AbstractSubcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.format.TextColor;

public class SubcommandManager extends AbstractSubcommandManager<Subcommand> implements CommandExecutor, TabCompleter {
	public SubcommandManager(String base, String perm, TextColor color, JavaPlugin plugin) {
		super(base, perm, color);
		plugin.getCommand(base).setExecutor(this);
		plugin.getCommand(base).setTabCompleter(this);
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
		Subcommand sc = super.parseForCommand(args);
		if (sc == null) {
			s.sendMessage("§cInvalid command! Are you using the right syntax? /" + base);
			return true;
		}
		
		args = super.reduceArgs(args, sc);
		if (check(sc, s, args)) {
			sc.run(s, args);
		}
		return true;
	}
	
	
	// Modular for tabcomplete to use
	private boolean check(Subcommand cmd, CommandSender s, boolean silent) {
		// If cmd permission exists, it overrides list permission
		String activePerm = cmd.getPermission() != null ? cmd.getPermission() : perm;
		if (activePerm != null && !s.hasPermission(activePerm)) {
			if (!silent) s.sendMessage("§cYou're missing the permission: " + activePerm);
			return false;
		}

		if ((cmd.getRunner() == SubcommandRunner.PLAYER_ONLY && !(s instanceof Player)) ||
				(cmd.getRunner() == SubcommandRunner.CONSOLE_ONLY && !(s instanceof ConsoleCommandSender))) {
			if (!silent) s.sendMessage("§cYou are the wrong type of user for this command!");
			return false;
		}
		return true;
	}
	
	private boolean check(Subcommand cmd, CommandSender s, String[] args) {
		if (!check(cmd, s, false)) return false;
		
		CommandArguments cargs = cmd.getArgs();
		if (args.length < cargs.getMin() && cargs.getMin() != -1) {
			s.sendMessage("§cThis command requires at least " + cargs.getMin() + " args but received " + args.length + ".");
			s.sendMessage(getCommandLine(cmd));
			return false;
		}
		
		if (args.length > cargs.getMax() && cargs.getMax() != -1) {
			s.sendMessage("§cThis command requires at most " + cargs.getMax() + " args but received " + args.length + ".");
			s.sendMessage(getCommandLine(cmd));
			return false;
		}
		
		return true;
	}
	
	public void registerCommandList(String key) {
		registerCommandList(key, null, null);
	}
	
	public void registerCommandList(String key, String perm, TextColor color) {
		handlers.put(key.toLowerCase(), new CmdList(key, base, perm, super.perm, handlers, aliases, this.color, color));
	}
	
	public AbstractSubcommand getCommand(String key) {
		return handlers.get(key.toLowerCase());
	}
	
	public Set<String> getKeys() {
		return handlers.keySet();
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command command, String label, String[] args) {
		if (!(s instanceof Player)) return null;
		
		if (perm != null && !s.hasPermission(perm)) return null;
		
		if (args.length == 1) {
			// Get all commands that can be run by user
			return handlers.values().stream()
					.filter(cmd -> check(cmd, s, true) && !cmd.isHidden() && cmd.getKey().length() > 0 && cmd.getKey().startsWith(args[0]))
					.map(cmd -> cmd.getKey())
					.toList();
		}
		else {
			if (args[0].isBlank() || StringUtils.isNumeric(args[0])) return null;
			
			// Only look for a subcommand if the first arg is not a number and not blank
			Subcommand cmd = handlers.get(args[0].toLowerCase());
			if (cmd == null || cmd.isHidden() || !cmd.isTabEnabled()) return null;
			
			if (cmd.overridesTab) {
				return cmd.getTabOptions(s, args);
			}
			
			CommandArguments ca = cmd.getArgs();
			Arg arg = CommandArguments.getCurrentArg(args, ca);
			if (arg == null || arg.getTabOptions() == null) return null;
			return arg.getTabOptions().stream().filter((str) -> {
				return str.startsWith(args[args.length - 1]);
			}).collect(Collectors.toList());
		}
	}
}

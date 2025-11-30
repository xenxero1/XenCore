package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.command.CommandSender;

import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdBCoreBroadcast extends Subcommand {
	private static Component error = Component.text("Must have a message to send!", NamedTextColor.RED);

	public CmdBCoreBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		aliases = new String[] {"broadcast"};
		args.setOverride("[msg]");
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, error);
		}
		else {
			// Send msg
			BungeeAPI.broadcast("<dark_red>[<red><bold>MLMC</bold</red>] <gray>" + SharedUtil.connectArgs(args));
		}
	}
}

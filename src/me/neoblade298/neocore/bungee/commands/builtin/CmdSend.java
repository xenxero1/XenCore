package me.neoblade298.neocore.bungee.commands.builtin;

import java.util.Optional;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdSend implements SimpleCommand {
	private static final Component usage = Component.text("Not enough arguments! /send {player} [server]", NamedTextColor.RED);
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("send")
            .plugin(plugin)
            .build();
        
        return meta;
	}
	
	@Override
	public void execute(Invocation inv) {
		String[] args = inv.arguments();
		if (args.length == 0) {
			Util.msg(inv.source(), usage);
			return;
		}
		Player p;
		if (args.length == 2) {
			Optional<Player> opt = BungeeCore.proxy().getPlayer(args[0]);
			if (opt.isEmpty()) {
				Util.msg(inv.source(), "<red>That player isn't online!");
				return;
			}
			p = opt.get();
		}
		else {
			p = (Player) inv.source();
		}
		
		Util.msg(inv.source(), "Send successful!");
		p.createConnectionRequest(BungeeCore.proxy().getServer(args[1]).get()).fireAndForget();
	}
	
	@Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }

}

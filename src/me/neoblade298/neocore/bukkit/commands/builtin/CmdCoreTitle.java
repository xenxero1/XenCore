package me.neoblade298.neocore.bukkit.commands.builtin;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class CmdCoreTitle extends Subcommand {

	public CmdCoreTitle(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player"));
		args.setOverride("[player] {--title x} {--subtitle y} {--in a} {--stay b} {--out c} ");
		args.setMin(1);
	}
	
	@Override
	public void run(CommandSender s, String[] args) {
		Player p = Bukkit.getPlayer(args[0]);
		String title = "";
		String subtitle = "";
		long fadeIn = 1;
		long stay = 3;
		long fadeOut = 1;
		Times times = Times.times(Duration.of(fadeIn, ChronoUnit.SECONDS), Duration.of(stay, ChronoUnit.SECONDS), Duration.of(fadeOut, ChronoUnit.SECONDS));
		
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase("--title")) {
				int size = 0;
				for (int j = i + 1; j < args.length; j++) {
					if (args[j].startsWith("--")) {
						i = j - 1; // Since i iterates after this
						break;
					}
					else {
						if (size > 0) title += " ";
						title += args[j];
						size++;
					}
				}
			}
			else if (arg.equalsIgnoreCase("--subtitle")) {
				int size = 0;
				for (int j = i + 1; j < args.length; j++) {
					if (args[j].startsWith("--")) {
						i = j - 1; // Since i iterates after this
						break;
					}
					else {
						if (size > 0) subtitle += " ";
						subtitle += args[j];
						size++;
					}
				}
			}
			else if (arg.equalsIgnoreCase("--in")) {
				fadeIn = Integer.parseInt(args[++i]);
			}
			else if (arg.equalsIgnoreCase("--stay")) {
				stay = Integer.parseInt(args[++i]);
			}
			else if (arg.equalsIgnoreCase("--out")) {
				fadeOut = Integer.parseInt(args[++i]);
			}
		}
		MiniMessage mini = NeoCore.miniMessage();
		Title t = Title.title(mini.deserialize(title), mini.deserialize(subtitle), times);
		p.showTitle(t);
	}
}

package me.neoblade298.neocore.bukkit.util;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import me.neoblade298.neocore.bukkit.NeoCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Util {
	private static Component prefix;
	
	static {
		prefix = Component.text("[", NamedTextColor.DARK_RED)
				.append(Component.text("MLMC", NamedTextColor.RED, TextDecoration.BOLD))
				.append(Component.text("]")).appendSpace();
	}
	
	public static void msgGroup(Collection<Player> s, Component msg, boolean hasPrefix) {
		for (CommandSender sender : s) {
			msg(sender, msg, hasPrefix);
		}
	}
	
	public static void msgGroup(Collection<Player> s, Component msg) {
		msgGroup(s, msg, true);
	}
	
	public static void msgGroupRaw(Collection<Player> s, Component msg) {
		msgGroup(s, msg, false);
	}
	
	public static void msgRaw(CommandSender s, String msg) {
		msg(s, NeoCore.miniMessage().deserialize(msg), false);
	}

	public static void msgRaw(CommandSender s, Component msg) {
		msg(s, msg, false);
	}

	public static void msg(CommandSender s, Component msg) {
		msg(s, msg, true);
	}

	public static void msg(CommandSender s, String msg) {
		msg(s, NeoCore.miniMessage().deserialize(msg), true);
	}

	public static void msg(CommandSender s, Component msg, boolean hasPrefix) {
		s.sendMessage(hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY));
	}
	
	public static void broadcast(String msg, boolean hasPrefix) {
		broadcast(Component.text(msg), hasPrefix);
	}
	
	public static void broadcast(Component msg, boolean hasPrefix) {
		msg = hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY);
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(msg);
		}
	}

	public static Location stringToLoc(String loc) {
		String args[] = loc.split(" ");
		World w = Bukkit.getWorld(args[0]);
		double x = Double.parseDouble(args[1]);
		double y = Double.parseDouble(args[2]);
		double z = Double.parseDouble(args[3]);
		float yaw = 0;
		float pitch = 0;
		if (args.length > 4) {
			yaw = Float.parseFloat(args[4]);
			pitch = Float.parseFloat(args[5]);
		}
		return new Location(w, x, y, z, yaw, pitch);
	}
	
	public static String locToString(Location loc) {
		return locToString(loc, true, false);
	}

	public static String locToString(Location loc, boolean round, boolean includePitch) {
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		String str = loc.getWorld().getName() + " " + x + " " + y + " " + z;
		if (round) {
			x = Math.round(x) + 0.5;
			y = Math.round(y) + 0.5;
			z = Math.round(z) + 0.5;
		}
		if (includePitch) {
			str += " " + loc.getYaw() + " " + loc.getPitch();
		}
		return str;
	}
    
    public static BukkitTask runTask(Runnable runnable, long delay) {
    	return new BukkitRunnable() {
    		public void run() {
    			runnable.run();
    		}
    	}.runTaskLater(NeoCore.inst(), delay);
    }

	public static void displayError(Player p, String error) {
		p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.7F);
		Util.msgRaw(p, Component.text(error, NamedTextColor.RED));
	}
}

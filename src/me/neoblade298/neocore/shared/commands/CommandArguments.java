package me.neoblade298.neocore.shared.commands;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

public class CommandArguments {
	private ArrayList<Arg> args = new ArrayList<Arg>();
	private int min = 0, max = 0;
	private String display = "";

	public CommandArguments(Arg... args) {
		add(args);
	}

	public CommandArguments add(Arg... args) {
		for (Arg arg : args) {
			this.args.add(arg);

			if (display.length() > 0) {
				display += " ";
			}

			if (arg.isRequired()) {
				display += "[" + arg.getDisplay() + "]";
				min++;
			}
			else {
				display += "{" + arg.getDisplay() + "}";
			}
			max++;
		}
		return this;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public String getDisplay() {
		return display;
	}

	public ArrayList<Arg> getArguments() {
		return args;
	}

	public void setOverride(String override) {
		this.display = override;
		min = -1;
		max = -1;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int size() {
		return args.size();
	}

	public static Arg getCurrentArg(String[] args, CommandArguments cargs) {
		// Args only start after 1, since args are for subcommands and the subcommand is
		// args[0]
		if (args.length < 2) return null;
		if (cargs.size() == 0) return null;
		if (args.length - 1 > cargs.size()) return null;

		int argsIdx = 1;
		int cargsIdx = 0;

		while (argsIdx != args.length - 1) {
			Arg arg = cargs.getArguments().get(cargsIdx);
			if (arg.isRequired() || argExists(arg, args[argsIdx])) {
				argsIdx++;
				cargsIdx++;
			}
			else {
				cargsIdx++;
			}
			
			// Gone past all args
			if (cargsIdx + 1 >= cargs.size()) {
				return null;
			}
		}
		return cargs.getArguments().get(cargsIdx);
	}
	
	private static boolean argExists(Arg arg, String argString) {
		if (arg.getType() == ArgType.PLAYER) {
			return Bukkit.getPlayer(argString) != null;
		}
		else if (arg.getType() == ArgType.NUMBER) {
			return StringUtils.isNumeric(argString);
		}
		else {
			if (arg.getTabOptions() == null) return false;
			
			return arg.getTabOptions().contains(argString.toLowerCase());
		}
	}
}

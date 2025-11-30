package me.neoblade298.neocore.bukkit.commandsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.shared.io.Section;

public class CommandSet {
	private String key;
	private ArrayList<CommandSetEntry> entries = new ArrayList<CommandSetEntry>();
	private HashMap<String, CommandSetVariable> variables = new HashMap<String, CommandSetVariable>();
	private int totalWeight = 0;
	
	public CommandSet(String key, Section cfg) {
		this.key = key;
		
		if (cfg.contains("variables")) {
			Section sec = cfg.getSection("variables");
			for (String var : sec.getKeys()) {
				variables.put(var, new CommandSetVariable(sec.getSection(var)));
			}
		}
		
		Section sec = cfg.getSection("entries");
		for (String entry : sec.getKeys()) {
			CommandSetEntry cse = new CommandSetEntry(sec.getSection(entry));
			entries.add(cse);
			totalWeight += cse.getWeight();
		}
	}
	
	public void run(String args[]) {
		int gen = NeoCore.gen.nextInt(totalWeight);
		Iterator<CommandSetEntry> iter = entries.iterator();
		CommandSetEntry e = null;
		while (gen >= 0 && iter.hasNext()) {
			e = iter.next();
			gen -= e.getWeight();
		}
		if (e == null) {
			Bukkit.getLogger().warning("[NeoCore] Failed to run command set " + key + " due to no entries.");
			return;
		}
		e.run(variables, args);
	}
	
	public String getKey() {
		return key;
	}
}

package me.neoblade298.neocore.bungee;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bungee.chat.ChatResponseHandler;
import me.neoblade298.neocore.bungee.commands.builtin.CmdBroadcast;
import me.neoblade298.neocore.bungee.commands.builtin.CmdHub;
import me.neoblade298.neocore.bungee.commands.builtin.CmdKickAll;
import me.neoblade298.neocore.bungee.commands.builtin.CmdMotd;
import me.neoblade298.neocore.bungee.commands.builtin.CmdMutableBroadcast;
import me.neoblade298.neocore.bungee.commands.builtin.CmdSend;
import me.neoblade298.neocore.bungee.commands.builtin.CmdSendAll;
import me.neoblade298.neocore.bungee.commands.builtin.CmdSilentBroadcast;
import me.neoblade298.neocore.bungee.commands.builtin.CmdSilentMutableBroadcast;
import me.neoblade298.neocore.bungee.commands.builtin.CmdTp;
import me.neoblade298.neocore.bungee.commands.builtin.CmdTphere;
import me.neoblade298.neocore.bungee.commands.builtin.CmdUptime;
import me.neoblade298.neocore.bungee.io.FileLoader;
import me.neoblade298.neocore.bungee.listeners.ChatListener;
import me.neoblade298.neocore.bungee.listeners.MainListener;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.chat.MiniMessageManager;
import me.neoblade298.neocore.shared.io.Config;
import me.neoblade298.neocore.shared.io.SQLManager;
import me.neoblade298.neocore.shared.util.GradientManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Plugin(id = "neocore", name = "NeoCore", version = "0.1.0-SNAPSHOT",
        url = "https://ml-mc.com", description = "Neo's core plugin for his suite of plugins", authors = {"Ascheladd"})
public class BungeeCore {
	public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("neocore:bungee");
	private static ProxyServer proxy;
	private static Logger logger;
	private static BungeeCore inst;
	private static List<String> announceList;
	private static Config announceCfg;
	private static Component announcements;
	private static String motd;
	private static File folder;
	private static MiniMessage mini;
	
	private static Component joinPrefix, leavePrefix;
	
	// Used for tab complete
	public static TreeSet<String> players = new TreeSet<String>();
	
	@Inject
	public BungeeCore(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        inst = this;
		BungeeCore.proxy = server;
		BungeeCore.logger = logger;
		folder = dataDirectory.toFile();
	}
	
	@Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        CommandManager mngr = proxy.getCommandManager();
        
        mngr.register(CmdBroadcast.meta(mngr, this), new CmdBroadcast());
        mngr.register(CmdSilentBroadcast.meta(mngr, this), new CmdSilentBroadcast());
        mngr.register(CmdMutableBroadcast.meta(mngr, this), new CmdMutableBroadcast());
        mngr.register(CmdSilentMutableBroadcast.meta(mngr, this), new CmdSilentMutableBroadcast());
        mngr.register(CmdHub.meta(mngr, this), new CmdHub());
        mngr.register(CmdMotd.meta(mngr, this), new CmdMotd());
        mngr.register(CmdTp.meta(mngr, this), new CmdTp());
        mngr.register(CmdTphere.meta(mngr, this), new CmdTphere());
        mngr.register(CmdUptime.meta(mngr, this), new CmdUptime());
        mngr.register(CmdSendAll.meta(mngr, this), new CmdSendAll());
        mngr.register(CmdSend.meta(mngr, this), new CmdSend());
        mngr.register(CmdKickAll.meta(mngr, this), new CmdKickAll());
        proxy.getEventManager().register(this, new MainListener());
        proxy.getEventManager().register(this, new ChatListener());
        proxy.getChannelRegistrar().register(IDENTIFIER);
        
		// Maybe doesn't work as of non-bungification
        GradientManager.load(Config.load(new File(NeoCore.inst().getDataFolder(), "gradients.yml")));

		Config cfg = Config.load(new File(folder, "config.yml"));
        // sql
		try {
	        SQLManager.load(cfg.getSection("sql"));
	        reload();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		mini = MiniMessage.miniMessage();
		joinPrefix = mini.deserialize("<dark_gray>[<green>+</green>] ");
		leavePrefix = mini.deserialize("<dark_gray>[<red>-</red>] ");
    }
    
    public static ProxyServer proxy() {
    	return proxy;
    }
    
    public static Logger logger() {
    	return logger;
    }
    
    public static File folder() {
    	return folder;
    }
    
    private void reload() throws IOException {
    	MiniMessageManager.reloadBungee();
    	announceCfg = Config.load(new File(folder, "announcements.yml"));
    	announceList = announceCfg.getStringList("announcements");

    	// Reload announcements
		Builder b = Component.text();
		if (announceList.size() > 0) {
			for (int i = 0; i < announceList.size(); i++) {
				b.append(Component.text("- ", NamedTextColor.GRAY))
				.append(Component.text(announceList.get(i) + (i + 1 == announceList.size() ? "" : "\n"), NamedTextColor.YELLOW));
			}
		}
		else {
			b.append(Component.text("- ", NamedTextColor.GRAY))
			.append(Component.text("None for now!", NamedTextColor.YELLOW));
		}
		announcements = b.build();
    }
    
    public static void sendMotd(CommandSource s) {
    	// First send top half of MOTD (Mostly static)
		s.sendMessage(mini.deserialize(motd.replaceAll("%ONLINE%", "" + proxy.getPlayerCount())));
		s.sendMessage(announcements);
    }
    
    public static void addAnnouncement(CommandSource s, String msg) {
    	announceList.add(msg);
    	announceCfg.set("announcements", announceList);
		announceCfg.save();
    }
    
    public static void removeAnnouncement(CommandSource s, int idx) {
    	announceList.remove(idx);
    	announceCfg.set("announcements", announceList);
		announceCfg.save();
    }
	
	public static Connection getConnection(String user) {
		return SQLManager.getConnection(user);
	}
	
	// All servers
	public static void sendPluginMessage(String[] msgs) {
		sendPluginMessage(proxy.getAllServers(), msgs);
	}
	
	public static void sendPluginMessage(String[] servers, String[] msgs) {
		ArrayList<RegisteredServer> list = new ArrayList<RegisteredServer>();
		for (String server : servers) {
			list.add(proxy.getServer(server).get());
		}
		sendPluginMessage(list, msgs);
	}
	
	public static void sendPluginMessage(Iterable<RegisteredServer> servers, String[] msgs) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		for (String msg : msgs) {
			out.writeUTF(msg);
		}
		for (RegisteredServer server : servers) {
			server.sendPluginMessage(IDENTIFIER, out.toByteArray());
		}
	}
	
	public static BungeeCore inst() {
		return inst;
	}
	
	public static void promptChatResponse(Player p, ChatResponseHandler... handler) {
		ChatListener.addChatHandler(p, 30, handler);
	}
	
	public static void promptChatResponse(Player p, int timeoutSeconds, ChatResponseHandler... handler) {
		ChatListener.addChatHandler(p, timeoutSeconds, handler);
	}
	
	public static void loadFiles(File load, FileLoader loader) {
		if (!load.exists()) {
			logger.warning("[BungeeCore] Failed to load file " + load.getPath() + ", file doesn't exist");
			return;
		}
		
		if (load.isDirectory()) {
			for (File file : load.listFiles()) {
				loadFiles(file, loader);
			}
		}
		else {
			Config cfg;
			cfg = Config.load(load);
			loader.load(cfg, load);
		}
	}
	
	@Subscribe
	public void onLogin(PostLoginEvent e) {
		Util.broadcast(joinPrefix.append(Component.text(e.getPlayer().getUsername(), NamedTextColor.GRAY)), false);
	}
	
	@Subscribe
	public void onLogout(DisconnectEvent e) {
		Util.broadcast(leavePrefix.append(Component.text(e.getPlayer().getUsername(), NamedTextColor.GRAY)), false);
	}
	
	public static MiniMessage miniMessage() {
		return mini;
	}
}

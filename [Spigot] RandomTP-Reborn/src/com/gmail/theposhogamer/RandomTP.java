package com.gmail.theposhogamer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.theposhogamer.Commands.Cmd;
import com.gmail.theposhogamer.Files.Database;
import com.gmail.theposhogamer.Files.Lang;
import com.gmail.theposhogamer.Listeners.Events;
import com.gmail.theposhogamer.Listeners.L_Unglitcher;
import com.gmail.theposhogamer.Util.CustomLoc;
import com.gmail.theposhogamer.Util.Menu;
import com.gmail.theposhogamer.Util.ReadableHelp;
import com.gmail.theposhogamer.Util.SignUtil;
import com.gmail.theposhogamer.Util.Timer;

import net.milkbowl.vault.economy.Economy;

public class RandomTP extends JavaPlugin {
	
	private static RandomTP plugin;
	public SignUtil sign;

	public boolean preciousStones = false;
	public boolean factions = false;
	public boolean vault = false;
	
	public Economy economy;
	public Lang lang;
	public Database db;
	
	public Menu menu;
	
	public String prefix = "§c[RandomTP] ";
	
	public boolean unglitcher = false;
	public HashMap<UUID,Long> glitchElapsed = new HashMap<UUID,Long>();
	public HashMap<UUID,Integer> glitchTries = new HashMap<UUID,Integer>();
	public HashMap<UUID,Location> supposedY = new HashMap<UUID,Location>();
	public HashMap<String,Long> cooldown = new HashMap<String,Long>(); //displayname,uuid - long
	public ArrayList<CustomLoc> signs = new ArrayList<CustomLoc>();
	
	public ReadableHelp help;
	
	@Override
	public void onEnable() {
		db=new Database(this);
		lang=new Lang(this);
		getConfig().options().copyDefaults(true);
		saveConfig();
		Timer startUp = new Timer();
		startUp.start();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		PluginManager bukkitManager = Bukkit.getServer().getPluginManager();
		sender.sendMessage("§e§l[RandomTP] §aStarting up & loading plugin data");
		sign = new SignUtil(this);
		sign.loadSigns();
		help = new ReadableHelp(this);
		help.reload();
		if(getConfig().getBoolean("AntiGlitcher.enabled")) {
			int time = getConfig().getInt("AntiGlitcher.taskeveryseconds")*20;
			int antiglitchdurationseconds = getConfig().getInt("AntiGlitcher.antiglitchdurationseconds");
			new L_Unglitcher(this);
			unglitcher=true;
			new BukkitRunnable() {
				public void run() {
					ArrayList<UUID> uuids = new ArrayList<UUID>(glitchElapsed.keySet());
					if(uuids.size() > 0) {
						Long current = System.currentTimeMillis();
						for(int i = 0; i<uuids.size(); i++) {
							UUID uuid = uuids.get(i);
							if(glitchElapsed.containsKey(uuid)) {
								Long secondsElapsed = (current-glitchElapsed.get(uuid))/1000;
								if(getServer().getPlayer(uuid) != null) {
									if(secondsElapsed > antiglitchdurationseconds) {
										removeFromCheckers(uuid);
									}
								} else {
									removeFromCheckers(uuid);
								}
							}
						}
					}
				}
			}.runTaskTimer(this, 20, time);
		}
		if(getConfig().getBoolean("SignBackup.enabled")) {
			int time = getConfig().getInt("SignBackup.taskeveryseconds")*20;
			new BukkitRunnable() {
				public void run() {
					sign.reloadSigns();
					sender.sendMessage("§e§l[RandomTP] §aBackuping RandomTP signs...");
				}
			}.runTaskTimer(this, time, time);
		}
		new Events(this);
		menu = new Menu(this);
		menu.registerItems();
		getCommand("rtp").setExecutor(new Cmd(this));
		if (bukkitManager.getPlugin("Vault") != null) {
			RegisteredServiceProvider <Economy> service = 
					getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (service != null) {
				economy = service.getProvider();
				sender.sendMessage("§e§l[RandomTP] §aLinked with Vault-Economy");
				vault = true;
			}
		}
		if (bukkitManager.getPlugin("Factions") != null && bukkitManager.getPlugin("MassiveCore") != null) {
			factions = true;
			sender.sendMessage("§e§l[RandomTP] §aLinked with Factions");
		}
		if (bukkitManager.getPlugin("PreciousStones") != null) {
			preciousStones = true;
			sender.sendMessage("§e§l[RandomTP] §aLinked with PreciousStones");
		}
		
		startUp.stop();
		sender.sendMessage("§e§l[RandomTP] §aHas been successfully "
				+ "loaded in " + startUp.getDuration() + " ms");

		plugin=this;
	}
	
	@Override
	public void onDisable() {
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§e§l[RandomTP] §cDisabling & saving information to database");
		sign.unloadSigns();
		sender.sendMessage("§e§l[RandomTP] §aInformation saved. Plugin will continue being disabled");
	}
	
	public static RandomTP getPlugin() {
		return plugin;
	}
	
	public void removeFromCheckers(UUID uuid) {
		glitchElapsed.remove(uuid);
		glitchTries.remove(uuid);
		supposedY.remove(uuid);
	}
	
}

package com.gmail.theposhogamer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.economy.Economy;

public class RandomTP extends JavaPlugin implements Listener {

	public static Plugin instance;

	public static FileConfiguration data;
	public static File dfile;

	public static ArrayList < Player > players = new ArrayList < Player > ();
	public static ArrayList < Player > pCheck = new ArrayList < Player > ();
	public static ArrayList < Player > inMortal = new ArrayList < Player > ();

	public static HashMap < Player,
	Location > pLoc = new HashMap < Player, Location > ();
	public static HashMap < Player, Integer > pY = new HashMap < Player, Integer > ();
	public static HashMap < Player, Integer > tries = new HashMap < Player, Integer > ();
	public static HashMap < Player, Integer > pMoney = new HashMap < Player, Integer > ();
	public static HashMap < Player, Integer > signCooldown = new HashMap < Player, Integer > ();

	public static HashMap < Player, Integer > warnings = new HashMap < Player, Integer > ();

	public static Economy economy;

	@Override
	public void onEnable() {
		
		//Register events
		Bukkit.getPluginManager().registerEvents(this, this);

		getConfig().options().copyDefaults(true);
		saveConfig();

		//Register the instance
		instance = this;

		//Setup the database.yml file, where RandomTP signs are gonna be saved
		try {
			setup(this);
		} catch(Exception Ignored) {
			System.out.println("[RandomTP-Reborn] An error ocurred while trying " + "to create the flat file database");
		}

		PluginManager bukkitManager = Bukkit.getServer().getPluginManager();

		//Check if for Vault
		if (bukkitManager.getPlugin("Vault") != null) {
			System.out.println("[RandomTP-Reborn] Linked with Vault-Economy!");
			RegisteredServiceProvider < Economy > service = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (service != null) {
				economy = service.getProvider();
				System.out.println("[RandomTP-Reborn] Linked with Vault-Economy!");
			}

		}

		//Check for Factions & MassiveCore
		if (bukkitManager.getPlugin("Factions") != null && bukkitManager.getPlugin("MassiveCore") != null) {
			CheckAmbient.factions = true;
			System.out.println("[RandomTP-Reborn] Linked with Factions!");
		}

		//Registering listeners
		new Events();
		new Menu();

		//Loading GUI menu
		Menu.registerItems();

		//Start cooldown task
		Cooldown.startTask();
		
		System.out.println("[RandomTP-Reborn] The plugin load process has been completed sucessfully. Runnining " + this.getDescription().getVersion().toString() + " version of the plugin.");
	}@Override
	public void onDisable() {
		//Disabling the plugin
		Cooldown.save();
		System.out.println("[RandomTP-Reborn] has been disabled, see you later.");
	}

	public void setup(Plugin p) throws IOException {
		dfile = new File(p.getDataFolder(), "database.yml");
		if (!dfile.exists()) {
			dfile.createNewFile();
		}
		data = YamlConfiguration.loadConfiguration(dfile);
	}

	public static void saveData() {
		try {
			data.save(dfile);
		}
		catch(IOException localIOException) {
			System.out.println("[RandomTP-Reborn] An error ocurred while trying " + "to save the flat file database");
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {

		if (cmd.getName().equalsIgnoreCase("randomtp")) {

			FileConfiguration cfg = getConfig();
			String noPerms = cfg.getString("Messages.NoPermission").replace("&", "§");
			String invalidWorld = cfg.getString("Messages.InvalidWorld").replace("&", "§");

			if (args.length == 0) {
				sender.sendMessage(" ");
				sender.sendMessage("§7§l§m---------[ §e§lRandom§c§lT§6§lP§7/§e§lReborn §7§l§m]---------");
				sender.sendMessage("§a- §7Version: §a" + this.getDescription().getVersion() + " §7Author: §a" + this.getDescription().getAuthors().get(0));
				sender.sendMessage("§a- §7Commands:");
				sender.sendMessage("§a- §7/randomtp tp <world> <distance>");
				sender.sendMessage("§a- §7/randomtp consoletp <world> <distance> <player>");
				sender.sendMessage("§a- §7/randomtp cooldown <seconds>");
				sender.sendMessage("§a- §7/randomtp list");
				sender.sendMessage("§a- §7/randomtp gui");
				return true;
			}
			if (args[0].equalsIgnoreCase("cooldown")) {
				
				if(args.length == 2) {
					if (!(sender instanceof Player) || !sender.hasPermission("randomtp.cooldown")) {
						return true;
					}
					Player p = (Player) sender;
					
					if(!signCooldown.containsKey(p)) {
						try {
							
							Integer seconds = Integer.valueOf(args[1]);
							signCooldown.put(p, seconds);
							sender.sendMessage(cfg.getString("Messages.ClickSign").replace("&", "§"));
							
						}catch(Exception ex) {
							sender.sendMessage(cfg.getString("Messages.NoNumber").replace("&", "§").replace("%number%", args[1]));
						}
					} else {
						sender.sendMessage(cfg.getString("Messages.AlreadyChoosing").replace("&", "§"));
					}
					
				} else {

					sender.sendMessage(cfg.getString("Messages.CorrectUsage.CooldownCommand").replace("&", "§"));
				}
				
			}
			if (args[0].equalsIgnoreCase("tp")) {

				if (! (sender instanceof Player)) {
					return true;
				}
				Player p = (Player) sender;
				if (p.hasPermission("randomtp.commandtp")) {

					String usage = cfg.getString("Messages.CorrectUsage.BasicCommand").replace("&", "§");
					//Checking if any variable is null
					try {
						String world = args[1];
						String distance = args[2];
						if (Bukkit.getWorld(world) == null) {
							p.sendMessage(invalidWorld);
							return true;
						}
						if (distance == null) {
							p.sendMessage(usage);
							return true;
						}
						//If no variable is null initiating teleportation
						RandomTP.initiateTeleport(p, Bukkit.getWorld(world), Integer.valueOf(distance));
					} catch(Exception Ignored) {
						p.sendMessage(usage);
					}
				} else {

					p.sendMessage(noPerms);

				}

			}
			if (args[0].equalsIgnoreCase("gui")) {
				if (! (sender instanceof Player)) {
					return true;
				}
				Player p = (Player) sender;
				if (p.hasPermission("randomtp.gui")) {
					Menu.openInventory(p);
				} else {
					sender.sendMessage(noPerms);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("consoletp")) {
				if (sender.hasPermission("randomtp.consoletp")) {
					String usage = cfg.getString("Messages.CorrectUsage.ConsoleCommand").replace("&", "§");
					try {
						//Checking for null variables
						String world = args[1];
						String distance = args[2];
						String player = args[3];
						if (Bukkit.getPlayer(player) == null) {
							sender.sendMessage(cfg.getString("Messages.OfflinePlayer").replace("&", "§").replace("%player%", player));

							return true;
						}
						if (Bukkit.getWorld(world) == null) {
							sender.sendMessage(invalidWorld);
							return true;
						}
						if (distance == null) {
							sender.sendMessage(usage);
							return true;
						}
						//Starting teleportation because no variable is null
						RandomTP.initiateTeleport(Bukkit.getPlayer(player), Bukkit.getWorld(world), Integer.valueOf(distance));
					} catch(Exception Ignored) {
						sender.sendMessage(usage);
					}
				} else {
					sender.sendMessage(noPerms);
				}
			}
			if (args[0].equalsIgnoreCase("list")) {
				if (! (sender instanceof Player)) {
					return true;
				}
				Player p = (Player) sender;
				if (p.hasPermission("randomtp.list")) {
					//Checking if page is null and procceding with the code
					try {
						
						if (args.length == 1) {
							//If player didn't specify any page chosing the first one
							if ((ArrayList < String > ) RandomTP.data.getStringList("Signs") != null) {
								ArrayList < String > signs = (ArrayList < String > ) RandomTP.data.getStringList("Signs");

								sender.sendMessage(" ");
								sender.sendMessage("§a► §6Actual signs in page §e1:");
								for (int i = 0; i < 5; i++) {
									if (signs.get(i) != null) {
										String[] arr = signs.get(i).split(",");
										String x = arr[0];
										String y = arr[1];
										String z = arr[2];
										String world = arr[3];
										p.sendMessage("§a► §6Sign[" + i + "]: §6Coordinates: §7" + x + "," + y + "," + z + "§6 Located in the world: §7" + world);
									}
								}
							}
						} else {
							//Else if player has specified a page going to that page if exists
							if ((ArrayList < String > ) RandomTP.data.getStringList("Signs") != null) {
								ArrayList < String > signs = (ArrayList < String > ) RandomTP.data.getStringList("Signs");
								//Doing 5 signs per page if there are 5 or less signs creating only one page
								if (signs.size() <= 5) {

									sender.sendMessage(" ");
									sender.sendMessage("§a► §6Actual signs in page §e1:");
									for (int i = 0; i < signs.size(); i++) {
										if (signs.get(i) != null) {
											String[] arr = signs.get(i).split(",");
											String x = arr[0];
											String y = arr[1];
											String z = arr[2];
											String world = arr[3];
											p.sendMessage("§a► §6Sign[" + i + "]: §6Coordinates: §7" + x + "," + y + "," + z + "§6 Located in the world: §7" + world);
										}

									}

								} else {
									
									//Else creating more pages
									double size = signs.size();
									double hojas = size / 5;
									int totales = (int) hojas;
									if (! ((hojas % 1) == 0)) {
										totales = totales + 1;
									}
									int hoja = Integer.valueOf(args[1]);

									int checker = 0;

									for (int i = 0; i < totales + 1; i++) {
										if (hoja == i) {
											sender.sendMessage(" ");
											sender.sendMessage("§a► §6Actual signs in page §e" + hoja + ":");
											try {
												for (int i2 = ((hoja * 5) - 5); i2 < (hoja * 5); i2++) {

													if (signs.get(i2) != null) {
														String[] arr = signs.get(i2).split(",");
														String x = arr[0];
														String y = arr[1];
														String z = arr[2];
														String world = arr[3];
														p.sendMessage("§a► §6Sign[" + i2 + "]: §6Coordinates: §7" + x + "," + y + "," + z + " §6Located in the world: §7" + world);
													}
												}
											} catch(Exception ignored) {}
										} else {
											checker = checker + 1;
										}
									}
									if (checker == totales + 1) {
										p.sendMessage("§6Page does not exist");
									}

								}

							}
						}
					} catch(Exception ignored) {}

				} else {
					p.sendMessage(noPerms);

				}
				return true;
			}

		}

		return true;
	}

	public static void initiateTeleport(Player p, World w, int maxblocks) {
		

		FileConfiguration cfg = instance.getConfig();

		players.add(p);
		
		p.sendMessage(cfg.getString("Messages.Searching").replace("&", "§"));
		
		CheckAmbient.returnLocation(p, w, maxblocks);
		new BukkitRunnable() {
			public void run() {
				if (RandomTP.tries.get(p) > 100) {
					cancel();
					players.remove(p);
					RandomTP.tries.remove(p);
					if (RandomTP.pMoney.get(p) != null) {
						int price = RandomTP.pMoney.get(p);
						RandomTP.economy.depositPlayer(p, price);
						p.sendMessage(cfg.getString("Messages.NoLocation").replace("&", "§").replace("%price%", price + ""));
						RandomTP.pMoney.remove(p);
					}
					p.sendMessage(cfg.getString("Messages.NotFound").replace("&", "§"));
				}
				if (! (pLoc.get(p) == null)) {
					cancel();
					Location loc = pLoc.get(p);
					pCheck.add(p);
					new BukkitRunnable() {
						public void run() {
							if (p != null && p.isOnline()) {
								if (pLoc.get(p) != null) {
									cancel();
									inMortal.add(p);
									p.teleport(loc);
									if (p.getLocation() != loc) {
										p.teleport(loc);
									}
									RandomTP.pMoney.remove(p);
									new BukkitRunnable() {
										public void run() {
											if (loc.getWorld().getName().equalsIgnoreCase(p.getWorld().getName())) {
												if (loc.distance(p.getLocation()) > 6) {
													cancelTaskAndInmortal(this, p);
													players.remove(p);
												}
											} else {
												cancelTaskAndInmortal(this, p);
												players.remove(p);
											}
										}
									}.runTaskTimer(instance, 20L, 20L);
								} else {
									cancelTaskAndInmortal(this, p);
									players.remove(p);
								}
							} else {
								cancel();
								players.remove(p);
							}
						}
					}.runTaskTimer(instance, 10L, 10L);
				}
			}
		}.runTaskTimer(instance, 10L, 10L);

	}

	public static void cancelTaskAndInmortal(BukkitRunnable b, Player p) {
		if (b != null) {
			b.cancel();
		}
		pLoc.remove(p);
		pCheck.remove(p);
		pY.remove(p);
		inMortal.remove(p);
		players.remove(p);
	}
}
package com.gmail.theposhogamer.Commands;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.gmail.theposhogamer.RandomTP;

public class Cmd implements CommandExecutor {
	
	private RandomTP plugin;
	
	public Cmd(RandomTP plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args) {

		if(cmd.getName().equalsIgnoreCase("rtp")) {
			if(args.length == 0) {
				if(sender.hasPermission("randomtp.basic")) {
					sender.sendMessage(" ");
					sender.sendMessage("§6§lRandomTP §7command list:");
					sender.sendMessage("§f» §b/rtp tp <player> <world> <distance> | random teleports "
							+ "the mentioned player to a random location with the mentioned setting values");
					sender.sendMessage("§f» §b/rtp list | Shows you a list of the RandomTP sign placed locations");
					sender.sendMessage("§f» §b/rtp gui | Opens you a RandomTP menu");
					sender.sendMessage("§f» §b/rtp setcooldown | Type this and then click "
							+ "a RandomTP sign to add a cooldown to it");
					sender.sendMessage("§f» §b/rtp version | Shows you the RandomTP current version");
				}
				return true;
			} else {
				if(args[0].equalsIgnoreCase("version")) {
					PluginDescriptionFile pdf = this.plugin.getDescription();
					sender.sendMessage(this.plugin.prefix+"§cSpigot plugin coded "
							+ "by @AnyOrdDev, version " + pdf.getVersion());
					return true;
				}
				if(args[0].equalsIgnoreCase("list")) {
					if(sender instanceof Player) {
						Player p = (Player)sender;
						if(p.hasPermission("randomtp.list")) {
							if(args.length != 2) {
								this.plugin.help.sendMessage(p, 0);
							} else {
								String args1 = args[1];
								if(this.plugin.sign.isNumeric(args1)) {
									this.plugin.help.sendMessage(p, Integer.valueOf(args1));
								} else {
									p.sendMessage(this.plugin.prefix+"§c"+args1+" is not a valid number of seconds");
								}
							}
						} else {
							sender.sendMessage(this.plugin.lang.getConfig()
									.getString("NOPERMISSION").replace("&", "§"));
						}
					}
					return true;
				} else if(args[0].equalsIgnoreCase("tp")) {
					if(sender.hasPermission("randomtp.tp")) {
						if(args.length == 4) {
							String player = args[1];
							String world = args[2];
							String distance = args[3];
							World w = this.plugin.getServer().getWorld(world);
							if(w != null) {
								Player p = this.plugin.getServer().getPlayer(player);
								if(p != null) {
									if(this.plugin.sign.isNumeric(distance)) {
										if(!this.plugin.sign.initiateTeleport(p, w, Integer.valueOf(distance), 0)) {
											sender.sendMessage(this.plugin.lang.getConfig()
													.getString("UNSUCCESSFULTELEPORT").replace("&", "§"));
										}
									} else {
										sender.sendMessage(this.plugin.lang.getConfig()
												.getString("CONSOLEINVALIDBLOCKSNUMBER").replace("&", "§"));
									}
								}
							} else {
								sender.sendMessage(this.plugin.lang.getConfig()
										.getString("UNEXISTENT").replace("&", "§"));
							}
						} else {
							sender.sendMessage(this.plugin.prefix + "§cUsage: /rtp <player> <world> <distance>");
						}
					} else {
						sender.sendMessage(this.plugin.lang.getConfig()
								.getString("NOPERMISSION").replace("&", "§"));
					}
					return true;
				} else if(args[0].equalsIgnoreCase("gui")) {
					if(sender instanceof Player) {
						Player p = (Player)sender;
						if(p.hasPermission("randomtp.gui")) {
							this.plugin.menu.openInventory(p);
						} else {
							sender.sendMessage(this.plugin.lang.getConfig()
									.getString("NOPERMISSION").replace("&", "§"));
						}
					}
					return true;
				} else if(args[0].equalsIgnoreCase("setcooldown")) {
					if(sender instanceof Player) {
						Player p = (Player)sender;
						if(p.hasPermission("randomtp.setcooldown")) {
							if(args.length == 2) {
								String number = args[1];
								if(this.plugin.sign.isNumeric(number)) {
									int cooldown = Integer.valueOf(number);
									UUID uuid = p.getUniqueId();
									this.plugin.sign.uuidCooldown.put(uuid, cooldown);
									p.sendMessage(this.plugin.prefix + "§aPlease now click "
											+ "the sign to add the " + number + " seconds cooldown");
								} else {
									p.sendMessage(this.plugin.prefix+"§c"+number+" is not a valid number of seconds");
								}
							} else {
								p.sendMessage(this.plugin.prefix + "§cUsage: /rtp setcooldown <number>");
							}
						} else {
							sender.sendMessage(this.plugin.lang.getConfig()
									.getString("NOPERMISSION").replace("&", "§"));
						}
						
					}
					return true;
				}
			}
			
		}
		return true;
	}
	
}

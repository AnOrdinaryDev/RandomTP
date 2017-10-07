package com.gmail.theposhogamer.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.theposhogamer.RandomTP;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.ps.PS;

import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.api.IApi;

public class SignUtil {
	
	private RandomTP plugin;
	
	public SignUtil(RandomTP main) {
		this.plugin = main;
	}
	
	public HashMap<UUID,Integer> uuidCooldown = new HashMap<UUID,Integer>();
	
	public void loadSigns() {
		ConsoleCommandSender sender = this.plugin.getServer().getConsoleSender();
		this.plugin.signs.clear();
		List<String> signs = this.plugin.db.getConfig().getStringList("Signs");
		if(signs != null) {
			int amount = 0;
			for(int i = 0; i<signs.size(); i++) {
				String customLoc = signs.get(i);
				this.plugin.signs.add(stringToCustomLocation(customLoc));
				amount++;
			}
			if(amount > 1) {
				sender.sendMessage(this.plugin.prefix + "§7" + amount + " signs were loaded");
			} else {
				sender.sendMessage(this.plugin.prefix + "§7" + amount + " sign was loaded");
			}
		} else {
			sender.sendMessage(this.plugin.prefix + "§7No signs to load were found");
		}
	}
	
	public void unloadSigns() {
		ArrayList<CustomLoc> signs = this.plugin.signs;
		List<String> toString = new ArrayList<String>();
		for(int i = 0; i<signs.size(); i++) {
			CustomLoc loc = signs.get(i);
			toString.add(customLocToString(loc));
		}
		this.plugin.db.getConfig().set("Signs", toString);
		this.plugin.db.save();
	}
	
	public void reloadSigns() {
		this.unloadSigns();
		this.loadSigns();
	}

	public boolean isSign(Location loc) {
		ArrayList<CustomLoc> signs = this.plugin.signs;
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		String world = loc.getWorld().getName();
		for(int i = 0; i<signs.size(); i++) {
			CustomLoc cloc = signs.get(i);
			if(cloc.x == x && cloc.y == y && cloc.z == z
					&& world.equalsIgnoreCase(cloc.world)) {
				return true;
			}
		}
		return false;
	}
	
	public CustomLoc getSign(Location loc) {
		ArrayList<CustomLoc> signs = this.plugin.signs;
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		String world = loc.getWorld().getName();
		for(int i = 0; i<signs.size(); i++) {
			CustomLoc cloc = signs.get(i);
			if(cloc.x == x && cloc.y == y && cloc.z == z
					&& world.equalsIgnoreCase(cloc.world)) {
				return cloc;
			}
		}
		return null;
	}
	
	public boolean removeSign(Location loc) {
		if(isSign(loc)) {
			CustomLoc customLoc = getSign(loc);
			this.plugin.signs.remove(customLoc);
			return true;
		}
		return false;
	}
	
	public CustomLoc createSign(Location loc, int blocks, int price, int cooldown, World w) {
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		String world = loc.getWorld().getName();
		return new CustomLoc(x,y,z,world, blocks, price, cooldown);
	}
	
	public String customLocToString(CustomLoc loc) {
		return loc.x + "," + loc.y + "," + loc.z + "," + 
			loc.world + "," + loc.blocks + "," + loc.price + "," + loc.cooldown;
	}
	
	
	
	public boolean setSignCooldown(Player p, Location loc, int cooldown) {
		loc = loc.getBlock().getLocation();
		if(this.isSign(loc)) {
			CustomLoc customLoc = this.getSign(loc);
			customLoc.cooldown = cooldown;
			p.sendMessage(this.plugin.prefix + "§7Cooldown "
					+ "has been set to " + cooldown + " second/s");
			return true;
		} else {
			p.sendMessage(this.plugin.prefix + "§7That is not a RandomTP sign");
			return false;
		}
	}
	
	public CustomLoc stringToCustomLocation(String customLoc) {
		String[] loc = customLoc.split(",");
		int x = Integer.valueOf(loc[0]);
		int y = Integer.valueOf(loc[1]);
		int z = Integer.valueOf(loc[2]);
		String world = loc[3];
		int blocks = Integer.valueOf(loc[4]);
		int price = Integer.valueOf(loc[5]);
		int cooldown = Integer.valueOf(loc[6]);
		return new CustomLoc(x,y,z,world, blocks, price, cooldown);
	}
	
	private Integer randomSymbol(int number, Random random) {
		int probability = random.nextInt(100);
		if (probability >= 50) {
			return (number * -1);
		}
		return number;
	}
	
	private List <Block> getNearbyBlocks(Location location, int radius) {
		List < Block > blocks = new ArrayList < Block > ();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}
	
	public boolean isBiome(Location loc, Biome b) {
		return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()) == b;
	}
	
	public boolean isWater(Location loc) {
		if (isBiome(loc, Biome.OCEAN) || isBiome(loc, Biome.DEEP_OCEAN)) {
			return true;
		}
		return false;
	}
	
	public boolean isNumeric(String str) {
		for (char c: str.toCharArray()) {
			if (!Character.isDigit(c)) return false;
		}
		return true;
	}
	
	public void safeTeleport(Player p, final Location loc) {
		Entity vehicle = p.getVehicle();
		if (vehicle != null) {
			if(!(vehicle instanceof Player)) {
				vehicle.remove();
			}
		}
    	p.setFallDistance(0);
		p.teleport(loc);
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable(){
            @Override
            public void run() {
            	p.setFallDistance(0);
        		p.teleport(loc);
            }
		}, 10L);
		
	}
	
	public boolean initiateTeleport(Player p, World w, int blocks, int price) {
		Location random = this.getRandomLocation(blocks, w);
		if(random==null) {
			return false;
		}
		if(this.plugin.unglitcher) {
			UUID uuid = p.getUniqueId();
			this.plugin.glitchElapsed.put(uuid, System.currentTimeMillis());
			this.plugin.supposedY.put(uuid, random);
			this.plugin.glitchTries.put(uuid, 0);
		}
		if(price != 0) {
			if(this.plugin.vault) {
				if (this.plugin.economy.getBalance(p) >= price) {
					this.plugin.economy.withdrawPlayer(p, price);
					p.sendMessage(this.plugin.lang.getConfig().getString("SUCCESSFULBUY").replace("&", "§")
							.replace("%variable%", String.valueOf(price)));
				} else {
					p.sendMessage(this.plugin.lang.getConfig().getString("INSSUFICIENTMONEY").replace("&", "§")
							.replace("%variable%", String.valueOf(price)));
					return false;
				}
			} else {
				p.sendMessage("§cAn internal error ocurred, "
						+ "the sign was created with vault economy but "
						+ "now it seems like vault went away");
				return false;
			}
		}
		this.safeTeleport(p, random);
		return true;
	}
	
	public void prepareForSign(Player p, Location loc) {

		UUID uuid = p.getUniqueId();
		if(this.isSign(loc)) {
			if(p.hasPermission("randomtp.usesigns")) {
				CustomLoc cloc = this.getSign(loc);
				if(this.uuidCooldown.containsKey(uuid)) {
					cloc.cooldown = this.uuidCooldown.get(uuid);
					p.sendMessage(this.plugin.prefix + "§aCooldown has been"
							+ " set to " + cloc.cooldown + " second/s");
					this.uuidCooldown.remove(uuid);
				} else {
					World w = this.plugin.getServer().getWorld(cloc.world);
					if(w!=null) {
						Location random = getRandomLocation(cloc.blocks, w);
						if(p!=null) {
							if(random!=null) {
								if(this.plugin.unglitcher) {
									this.plugin.glitchElapsed.put(uuid, System.currentTimeMillis());
									this.plugin.supposedY.put(uuid, random);
									this.plugin.glitchTries.put(uuid, 0);
								}
								if(cloc.price != 0) {
									if(this.plugin.vault) {
										if (this.plugin.economy.getBalance(p) >= cloc.price) {
											this.plugin.economy.withdrawPlayer(p, cloc.price);
											p.sendMessage(this.plugin.lang.getConfig()
													.getString("SUCCESSFULBUY").replace("&", "§")
													.replace("%variable%", String.valueOf(cloc.price)));
										} else {
											p.sendMessage(this.plugin.lang.getConfig()
													.getString("INSSUFICIENTMONEY").replace("&", "§")
													.replace("%variable%", String.valueOf(cloc.price)));
											return;
										}
									} else {
										p.sendMessage("§cAn internal error ocurred, "
												+ "the sign was created with vault economy but "
												+ "now it seems like vault went away");
										return;
									}
								}
								if (cloc.cooldown != 0) {
									String joined = cloc.world+cloc.x+cloc.y+cloc.z+","+p.getUniqueId().toString();
									Long nowtime = System.currentTimeMillis();
									if(this.plugin.cooldown.containsKey(joined)) {
										Long time = this.plugin.cooldown.get(joined);
										Long elapsedSeconds = (nowtime-time)/1000;
										if(elapsedSeconds >= cloc.cooldown) {
											this.plugin.cooldown.remove(joined);
										} else {
											Long left = cloc.cooldown-elapsedSeconds;
											p.sendMessage(this.plugin.lang.getConfig()
													.getString("COOLDOWNLEFT").replace("&", "§")
													.replace("%variable%", String.valueOf(left)));
											return;
										}
									} else {
										this.plugin.cooldown.put(joined, nowtime);
									}
								}
								this.safeTeleport(p, random);
								p.sendMessage(this.plugin.lang.getConfig().getString("SUCCESSTELEPORT").replace("&", "§"));
							} else {
								p.sendMessage(this.plugin.lang.getConfig().getString("UNSUCCESSFULTELEPORT").replace("&", "§"));
							}
						}
					} else {
						p.sendMessage(this.plugin.lang.getConfig().getString("UNEXISTENT").replace("&", "§"));
					}
				}
				
			} else {
				p.sendMessage(this.plugin.lang.getConfig().getString("NOPERMISSION").replace("&", "§"));
			}
			
		} else {
			if(this.uuidCooldown.containsKey(uuid)) {
				this.uuidCooldown.remove(uuid);
				p.sendMessage(this.plugin.prefix + "§cThat isn't a RandomTP sign");
			}
		}
		
		
		
	}
	
	public Location getRandomLocation(int blocks, World w) {

		Random random = new Random();
		Location loc = null;
		int tries = 0;
		boolean found = false;
		
		while(!found) {
			boolean isValid = true;
			
			int x = randomSymbol(random.nextInt(blocks), random);
			int z = randomSymbol(random.nextInt(blocks), random);
			int y = w.getHighestBlockYAt(x, z)+2;
			Location probLoc = new Location(w, x, y, z);
			if(isValid) {
				if(isWater(probLoc)) {
					isValid = false;
				}
			}
			if(isValid) {
				if(this.plugin.preciousStones) {
					IApi papi = PreciousStones.API();
					if(papi.isFieldProtectingArea(FieldFlag.ALL, loc) == true
							|| papi.isPStone(loc)== true) {
						isValid = false;
					}
				}
			}
			if(isValid) {
				if(this.plugin.factions) {
					Faction faction = null;
					faction = BoardColl.get().getFactionAt(PS.valueOf(loc));
					FactionColl fColl = FactionColl.get();
					if (!(faction == fColl.getNone() || faction == 
							fColl.getSafezone() || faction == fColl.getWarzone())) {
						isValid = false;
					}
				}
			}
			if(isValid) {
				List<Block> list = getNearbyBlocks(probLoc.add(0, -2, 0), 4);
				for (int i = 0; i < list.size(); i++) {
					Material type = list.get(i).getType();
					if (type == Material.WATER || type == Material.STATIONARY_WATER ||
							type == Material.LAVA || type == Material.STATIONARY_LAVA ||
									type == Material.LEAVES || type == Material.LEAVES_2) {
						isValid = false;
					}
				}
			}
			
			if(tries > 1000) {
				found = true;
			}
			if(isValid) {
				loc = probLoc;
				found = true;
			}
			
			tries++;
		}
		return loc;
		
	}
	
}

package com.gmail.theposhogamer;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateSign {
	
	public static void createSign(Location loc, int maxblocks, String w, String world, boolean econ, int cost) {
		
		//Saving sign to config 
		
		ArrayList<String> signs = null;
		
		if(RandomTP.data.getStringList("Signs") == null) {
			signs = new ArrayList<String>();
		} else {
			signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
		}
		
		String variable = loc.getBlockX() + "," + loc.getBlockY() + "," + 
		loc.getBlockZ() + "," + w + "," + maxblocks + "," + world + "," + econ + "," + cost;
		
		signs.add(variable);
		
		RandomTP.data.set("Signs", signs);
		RandomTP.saveData();
		
	}
	
	public static boolean isRandomTPSign(int x, int y, int z, String world) {
		
		//Checking if it is a randomtp sign
		
		if(RandomTP.data.getStringList("Signs") == null) {
			return false;
		} else {
			ArrayList<String> signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			for(int i = 0; i<signs.size(); i++) {
				String sign = signs.get(i);
				if(sign.startsWith(x + "," + y + "," + z + "," + world)) {
					return true;
				}
			}
		}
		return false;
		
	}
	
	public static String getWorld(int x, int y, int z, String world) {
		
		//Getting sign world
		
		String w = null;
		if(!(RandomTP.data.getStringList("Signs") == null))  {
			ArrayList<String> signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			for(int i = 0; i<signs.size(); i++) {
				String sign = signs.get(i);
				if(sign.startsWith(x + "," + y + "," + z + "," + world)) {
					String[] arr = sign.split(",");
					w = arr[5];
				}
			}
		}
		return w;
	}
	
	public static boolean getEcon(int x, int y, int z, String world) {
		
		//Getting economy
		
		boolean w = false;
		if(!(RandomTP.data.getStringList("Signs") == null))  {
			ArrayList<String> signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			for(int i = 0; i<signs.size(); i++) {
				String sign = signs.get(i);
				if(sign.startsWith(x + "," + y + "," + z + "," + world)) {
					String[] arr = sign.split(",");
					w = Boolean.valueOf(arr[6]);
				}
			}
		}
		return w;
	}
	
	public static Integer getPrice(int x, int y, int z, String world) {
		
		//Getting the price
		
		int w = 0;
		if(!(RandomTP.data.getStringList("Signs") == null))  {
			ArrayList<String> signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			for(int i = 0; i<signs.size(); i++) {
				String sign = signs.get(i);
				if(sign.startsWith(x + "," + y + "," + z + "," + world)) {
					String[] arr = sign.split(",");
					w = Integer.valueOf(arr[7]);
				}
			}
		}
		return w;
	}
	
	public static Integer getCooldown(int x, int y, int z, String world) {
		
		//Getting the cooldown
		
		int w = 0;
		if(!(RandomTP.data.getStringList("Signs") == null))  {
			ArrayList<String> signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			for(int i = 0; i<signs.size(); i++) {
				String sign = signs.get(i);
				if(sign.startsWith(x + "," + y + "," + z + "," + world)) {
					String[] arr = sign.split(",");
					w = Integer.valueOf(arr[8]);
				}
			}
		}
		return w;
	}
	
	public static boolean hasCooldown(int x, int y, int z, String world) {
		
		//Checking if has cooldown
		
		if(!(RandomTP.data.getStringList("Signs") == null))  {
			ArrayList<String> signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			for(int i = 0; i<signs.size(); i++) {
				String sign = signs.get(i);
				if(sign.startsWith(x + "," + y + "," + z + "," + world)) {
					String[] arr = sign.split(",");
					if(arr.length == 9) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
		return false;
	}
	
	public static void setCooldown(int x, int y, int z, String world, Integer seconds, CommandSender sender) {
		
		//Setting cooldown
		
		if(CreateSign.isRandomTPSign(x, y, z, world)) {
			
			ArrayList<String> signs = null;
			
			if(RandomTP.data.getStringList("Signs") == null) {
				signs = new ArrayList<String>();
			} else {
				signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			}
			
			Location loc = new Location(Bukkit.getWorld(world),x,y,z);
			
			String variable = loc.getBlockX() + "," + loc.getBlockY() + "," + 
			loc.getBlockZ() + "," + loc.getWorld().getName();
			
			for(int i = 0; i<signs.size(); i++) {
				
				String sign = signs.get(i);
				if(sign.startsWith(variable)) {
					
					String unparsed = sign;
					unparsed = sign+","+seconds;
					signs.remove(sign);
					signs.add(unparsed);
					sender.sendMessage("§aCooldown of " + seconds + " second/s was added succesfully.");
				}
				
			}
			
			RandomTP.data.set("Signs", signs);
			RandomTP.saveData();
			
		}
	}
	
	public static Integer getMaxBlocks(int x, int y, int z, String world) {
		
		//Getting maxblocks
		
		Integer w = null;
		if(!(RandomTP.data.getStringList("Signs") == null))  {
			ArrayList<String> signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			for(int i = 0; i<signs.size(); i++) {
				String sign = signs.get(i);
				if(sign.startsWith(x + "," + y + "," + z + "," + world)) {
					String[] arr = sign.split(",");
					w = Integer.valueOf(arr[4]);
				}
			}
		}
		return w;
	}
	
	public static Sign getSign(int x, int y, int z, String world) {
		
		//Getting sign
		
		Block block = Bukkit.getServer().getWorld(world).getBlockAt(x, y, z);
		BlockState state = block.getState();
		if (!(state instanceof Sign)) {
		    return null;
		} else {
			Sign sign = (Sign) state;
			return sign;
		}
		
		
		
	}
	
	public static void removeSign(int x, int y, int z, String world, Player p) {
		
		//Removing sign
		
		if(!(RandomTP.data.getStringList("Signs") == null))  {
			ArrayList<String> signs = (ArrayList<String>) RandomTP.data.getStringList("Signs");
			for(int i = 0; i<signs.size(); i++) {
				String sign = signs.get(i);
				if(sign.startsWith(x + "," + y + "," + z + "," + world)) {
					String loc = signs.get(i);
					signs.remove(loc);
					RandomTP.data.set("Signs", signs);
					RandomTP.saveData();
					p.sendMessage(RandomTP.instance.getConfig().
							getString("Messages.RemovedSign").replace("&", "§"));
				}
			}
		}
	}
	
}

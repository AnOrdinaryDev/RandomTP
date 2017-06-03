package com.gmail.theposhogamer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.SimpleChunkLocation;

import com.gmail.theposhogamer.Integration.Kingdoms;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.ps.PS;

import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;

public class CheckAmbient {

	public static boolean factions = false;
	public static boolean pstones = false;
	public static boolean kingdoms = false;

	static Kingdoms kingdomsClass = new Kingdoms();
	
	public static void returnLocation(Player p, World w, int maxblocks) {

		Random random = new Random();
		
		//Setting player tries to 0
		RandomTP.tries.put(p, 0);

		new BukkitRunnable() {
			@SuppressWarnings({ "static-access" })
			public void run() {

				if(RandomTP.tries.get(p) != null) {
					if (RandomTP.tries.get(p) > 200) {
						cancel();
					} else {
						RandomTP.tries.put(p, RandomTP.tries.get(p) + 1);
					}
					
					int x = randomSymbol(random.nextInt(maxblocks));
					int z = randomSymbol(random.nextInt(maxblocks));
					int y = w.getHighestBlockYAt(x, z);

					Location loc = new Location(w, x, y, z);

					boolean checker = false;

					//Checking if is ocean
					if (isWater(loc)) {
						checker = true;
					}
					//Checking for near solid blocks
					if (loc.add(0, 1, 0).getBlock().getType().isSolid() == true || loc.add(0, -1, 0).getBlock().getType().isSolid() == true || loc.getBlock().getType().isSolid() == true) {
						checker = true;
					}
					//Checking if near blocks are water or lava
					List < Block > list = getNearbyBlocks(loc, 4);
					for (int i = 0; i < list.size(); i++) {
						Block b = list.get(i);
						if (b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER || b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA || b.getType() == Material.LEAVES || b.getType() == Material.LEAVES_2) {
							checker = true;
						}
					}
					//Checking for PreciousStones 
					if(pstones == true) {
						
						if(PreciousStones.API().isFieldProtectingArea(FieldFlag.ALL, loc)== true
								|| PreciousStones.API().isPStone(loc)== true) {
							checker = true;
						}
						
					}

					//Checking for factions
					if (kingdoms == true) {
						SimpleChunkLocation locs = new SimpleChunkLocation(loc.getChunk());
						Land land = kingdomsClass.getManager().getLandManager().getOrLoadLand(locs);
						
						if(land.getOwner() != null) {
							checker = true;
							System.out.println(land.getOwner());
						}
						
					}
					//Checking for factions
					if (factions == true) {

						Faction faction = null;
						faction = BoardColl.get().getFactionAt(PS.valueOf(loc));
						if (! (faction == FactionColl.get().getNone() || faction == FactionColl.get().getSafezone() || faction == FactionColl.get().getWarzone())) {
							checker = true;
						}

					}
					/*If it found any dagerous place from the previous mentioned one's it will research and research 
					until it finds any safe location with a limit of 100 tries if not there will be no safe location*/ 
					
					if (checker == false) {
						cancel();
						RandomTP.pLoc.put(p, getCenterOfaBlock(loc));
						Location newloc = new Location(w, x, y, z);
						RandomTP.pY.put(p, (int) newloc.getY());
						RandomTP.warnings.put(p, 0);
						p.sendMessage(RandomTP.instance.getConfig().getString("Messages.Found").replace("&", "§"));
					}
					
				}
				
				
				
			}
		}.runTaskTimer(RandomTP.instance, 0L, 0L);

	}

	public static Location getCenterOfaBlock(Location l) {
		
		//Getting the center of a block
		
		Location m = l;
		
		if (m.getBlock().getX() > 0) {
			m.add(0.5, 0, 0);
		}
		if (m.getBlock().getZ() > 0) {
			m.add(0, 0, 0.5);
		}
		if (m.getBlock().getX() < 0) {
			m.add(0.5, 0, 0);
		}
		if (m.getBlock().getZ() < 0) {
			m.add(0, 0, 0.5);
		}
		return m;
	}

	public static Integer randomSymbol(int number) {

		//Converting an integer randomly from positive to negative and viceversa
		
		Random random = new Random();
		int probability = random.nextInt(100);
		if (probability >= 50) {
			if (!(number <= 0)) {
				return (number * -1);
			} else {
				return number+1;
			}
		}
		return number;

	}

	public static boolean isWater(Location loc) {
		//Checking if biome is ocean
		if (isBiome(loc, Biome.OCEAN) || isBiome(loc, Biome.DEEP_OCEAN)) {
			return true;
		}
		return false;
	}

	public static boolean isBiome(Location loc, Biome b) {
		//Biome checker
		return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ()) == b;
	}

	public static List < Block > getNearbyBlocks(Location location, int radius) {
		//Getting near blocks in a radius of 4.
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

}
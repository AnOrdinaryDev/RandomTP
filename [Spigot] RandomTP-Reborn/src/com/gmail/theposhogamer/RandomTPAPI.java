package com.gmail.theposhogamer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class RandomTPAPI {
	
	public static void teleportRandom(Player player, World world, Integer blocksDistance) {
		RandomTP.initiateTeleport(player, world, blocksDistance);
	}
	
	public static void getCenterOfBlock(Location location) {
		CheckAmbient.getCenterOfaBlock(location);
	}
	
	public static boolean isBiome(Location location, Biome biome) {
		return CheckAmbient.isBiome(location, biome);
	}
	
	public static boolean isOcean(Location location) {
		return CheckAmbient.isWater(location);
	}
	
}

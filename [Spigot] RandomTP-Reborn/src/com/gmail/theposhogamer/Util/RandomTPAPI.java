package com.gmail.theposhogamer.Util;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.theposhogamer.RandomTP;

public class RandomTPAPI {
	
	public static void teleportToRandom(Player p, int blocks, World w) {
		Location loc = RandomTP.getPlugin().sign.getRandomLocation(blocks, w);
		if(RandomTP.getPlugin().unglitcher) {
			UUID uuid = p.getUniqueId();
			RandomTP.getPlugin().glitchElapsed.put(uuid, System.currentTimeMillis());
			RandomTP.getPlugin().supposedY.put(uuid, loc);
			RandomTP.getPlugin().glitchTries.put(uuid, 0);
		}
		RandomTP.getPlugin().sign.safeTeleport(p, loc);
	}
	
	public static Location getRandomLocation(int blocks, World w) {
		Location loc = RandomTP.getPlugin().sign.getRandomLocation(blocks, w);
		return loc;
	}
	
	public static boolean isRandomTPSign(Location loc) {
		return RandomTP.getPlugin().sign.isSign(loc.getBlock().getLocation());
	}
	
	public static CustomLoc getRandomTPSign(Location loc) {
		return RandomTP.getPlugin().sign.getSign(loc.getBlock().getLocation());
	}
	
}

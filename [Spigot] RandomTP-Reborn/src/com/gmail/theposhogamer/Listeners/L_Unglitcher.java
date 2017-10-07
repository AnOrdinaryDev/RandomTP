package com.gmail.theposhogamer.Listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.theposhogamer.RandomTP;

public class L_Unglitcher implements Listener {
	private RandomTP plugin;
	
	public L_Unglitcher(RandomTP main) {
		plugin = main;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	private void onLogout(PlayerQuitEvent e) {
		plugin.removeFromCheckers(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (plugin.glitchTries.containsKey(uuid)) {
			if (plugin.supposedY.containsKey(uuid)) {
				Location loc = p.getLocation();
				Location supposed = plugin.supposedY.get(uuid);
				int warns = plugin.glitchTries.get(uuid);
				if ((loc.getX() < supposed.getX()) && (loc.getX() > ((supposed.getX() - 0.5)))) {
					//Checking for block glitch
					plugin.glitchTries.put(uuid, warns+1);
					if (warns >= 25) {
						//It seems you got glitched on a block so let's help you
						plugin.glitchTries.remove(uuid);
						plugin.supposedY.remove(uuid);
						p.teleport(p.getLocation().add(0, 1.25, 0));
						p.setFallDistance(0);
						p.setHealth(p.getMaxHealth());
					}
				} else if ((loc.getY() < supposed.getY()) && (loc.getY() > ((supposed.getY() - 0.5)))) {
					//Checking for block glitch
					plugin.glitchTries.put(uuid, warns+1);
					if (warns >= 25) {
						//It seems you got glitched on a block so let's help you
						plugin.glitchTries.remove(uuid);
						plugin.supposedY.remove(uuid);
						p.teleport(p.getLocation().add(0, 1.25, 0));
						p.setFallDistance(0);
						p.setHealth(p.getMaxHealth());
					}
				} else if ((loc.getZ() < supposed.getZ()) && (loc.getZ() > ((supposed.getZ() - 0.5)))) {
					//Checking for block glitch
					plugin.glitchTries.put(uuid, warns+1);
					if (warns >= 25) {
						//It seems you got glitched on a block so let's help you
						plugin.glitchTries.remove(uuid);
						plugin.supposedY.remove(uuid);
						p.teleport(p.getLocation().add(0, 1.25, 0));
						p.setFallDistance(0);
						p.setHealth(p.getMaxHealth());
					}
				}
			}
		}

	}
	
}

package com.gmail.theposhogamer;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class Events implements Listener {

	public Events() {
		Plugin instance = RandomTP.instance;
		instance.getServer().getPluginManager().registerEvents(this, instance);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {

		Player p = e.getPlayer();

		ArrayList < Player > pCheck = RandomTP.pCheck;
		HashMap < Player,
		Integer > pY = RandomTP.pY;
		HashMap < Player,
		Integer > warnings = RandomTP.warnings;

		if (pCheck.contains(p)) {
			if (pY.containsKey(p)) {
				int supposed = pY.get(p);
				int warns = warnings.get(p);
				if ((p.getLocation().getY() < supposed) && (p.getLocation().getY() > ((supposed - 0.5)))) {
					//Checking for block glitch
					warnings.put(p, warns + 1);
				}
				if (warns > 10) {
					//It seems you got glitched on a block so let's help you
					pCheck.remove(p);
					pY.remove(p);
					p.teleport(p.getLocation().add(0, 1, 0));
				}
			}
		}

	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		RandomTP.cancelTaskAndInmortal(null, e.getPlayer());
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		
		ArrayList<Player> players = RandomTP.players;
		if (players.contains(event.getPlayer())) {
			if (event.isCancelled()) {
				return;
			}
			Location loc = event.getTo();
			if (loc == null) {
				return;
			}
			World world = loc.getWorld();
			Chunk chunk = world.getChunkAt(loc);
			if (!world.isChunkLoaded(chunk.getX(), chunk.getZ())) {
				world.loadChunk(chunk);
			}
			world.refreshChunk(chunk.getX(), chunk.getZ());
			players.remove(event.getPlayer());
		}
	}

	@SuppressWarnings("deprecation")@EventHandler
	public void onDamage(EntityDamageEvent e) {

		if (e.getEntity() instanceof Player) {

			Player p = (Player) e.getEntity();
			if (RandomTP.inMortal.contains(p)) {
				e.setDamage(0);
				e.setCancelled(true);
			}

		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		Player p = e.getPlayer();
		Block block = e.getBlock();
		
		//Breaking randomtp sign event
		CreateSign.removeSign(block.getX(), block.getY(), block.getZ(), block.getWorld().getName(), p);

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		//Interacting sign
		
		FileConfiguration cfg = RandomTP.instance.getConfig();
		String noPerms = cfg.getString("Messages.NoPermission").replace("&", "§");
		String invalidWorld = cfg.getString("Messages.InvalidWorld").replace("&", "§");
		
		Player p = e.getPlayer();
		
		if ((e.getClickedBlock().getState() instanceof Sign)) {
			Sign s = (Sign) e.getClickedBlock().getState();
			Boolean is = CreateSign.isRandomTPSign(s.getX(), s.getY(), s.getZ(), s.getWorld().getName());
			if (is) {
				if (Bukkit.getWorld(CreateSign.getWorld(s.getX(), s.getY(), s.getZ(), s.getWorld().getName())) == null) {
					p.sendMessage(invalidWorld);
					return;
				}
				if (e.getPlayer().hasPermission("randomtp.usesign")) {
					if (CreateSign.getEcon(s.getX(), s.getY(), s.getZ(), s.getWorld().getName()) == false) {
						RandomTP.initiateTeleport(p, Bukkit.getWorld(CreateSign.getWorld(s.getX(), s.getY(), s.getZ(), s.getWorld().getName())), CreateSign.getMaxBlocks(s.getX(), s.getY(), s.getZ(), s.getWorld().getName()));
					} else {

						int price = CreateSign.getPrice(s.getX(), s.getY(), s.getZ(), s.getWorld().getName());
						RandomTP.pMoney.put(p, price);
						if (RandomTP.economy.getBalance(p) >= price) {
							RandomTP.economy.withdrawPlayer(p, price);
							e.getPlayer().sendMessage(cfg.getString("Messages.Economy").replace("&", "§").replace("%price%", price + ""));
							RandomTP.initiateTeleport(p, Bukkit.getWorld(CreateSign.getWorld(s.getX(), s.getY(), s.getZ(), s.getWorld().getName())), CreateSign.getMaxBlocks(s.getX(), s.getY(), s.getZ(), s.getWorld().getName()));
						} else {
							p.sendMessage(cfg.getString("Messages.NotEnough").replace("&", "§").replace("%price%", price + ""));
						}

					}
				} else {
					p.sendMessage(noPerms);
				}
			}
		}

	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {

		if (e.getLine(0).equalsIgnoreCase("[randomtp]")) {

			FileConfiguration cfg = RandomTP.instance.getConfig();
			String noPerms = cfg.getString("Messages.NoPermission").replace("&", "§");
			
			Player p = e.getPlayer();
			
			//Creating sign
			if (e.getPlayer().hasPermission("randomtp.createsign")) {
				BlockState state = e.getBlock().getState();
				Sign sign = (Sign) state;
				sign.update();
				boolean econ = true;
				int econprice = 0;
				if (e.getLine(3).isEmpty()) {
					econ = false;
				} else {
					econprice = Integer.valueOf(e.getLine(3));
				}
				if (RandomTP.economy == null && !e.getLine(3).isEmpty()) {
					econ = false;
					p.sendMessage(cfg.getString("Messages.UnableToCreateSign").replace("&", "§"));
				}
				CreateSign.createSign(e.getBlock().getLocation(), Integer.valueOf(e.getLine(1)), p.getWorld().getName(), e.getLine(2), econ, econprice);
				ArrayList < String > lines = (ArrayList < String > ) cfg.getStringList("SignFormat");
				if (econ == true) {
					e.setLine(0, lines.get(0).replace("&", "§").replace("%distance%", e.getLine(1)).replace("%price%", econprice + ""));
					e.setLine(1, lines.get(1).replace("&", "§").replace("%distance%", e.getLine(1)).replace("%price%", econprice + ""));
					e.setLine(2, lines.get(2).replace("&", "§").replace("%distance%", e.getLine(1)).replace("%price%", econprice + ""));
					e.setLine(3, lines.get(3).replace("&", "§").replace("%distance%", e.getLine(1)).replace("%price%", econprice + ""));
				} else {
					e.setLine(0, lines.get(0).replace("&", "§").replace("%distance%", e.getLine(1)).replace("%price%", "§cN/A"));
					e.setLine(1, lines.get(1).replace("&", "§").replace("%distance%", e.getLine(1)).replace("%price%", "§cN/A"));
					e.setLine(2, lines.get(2).replace("&", "§").replace("%distance%", e.getLine(1)).replace("%price%", "§cN/A"));
					e.setLine(3, lines.get(3).replace("&", "§").replace("%distance%", e.getLine(1)).replace("%price%", "§cN/A"));
				}
				sign.update();
				p.sendMessage(cfg.getString("Messages.CreatedSign").replace("&", "§"));
			}
			else if (!p.hasPermission("randomtp.createsign")) {
				e.getPlayer().sendMessage(noPerms);
				e.setCancelled(true);
			}

		}

	}

}
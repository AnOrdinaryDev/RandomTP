package com.gmail.theposhogamer.Listeners;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.gmail.theposhogamer.RandomTP;
import com.gmail.theposhogamer.Util.CustomLoc;

public class Events implements Listener {
	
	private RandomTP plugin;
	
	public Events(RandomTP main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	private void onSignChange(SignChangeEvent e) {
		String line0 = e.getLine(0); //PREFIX
		if (line0.equalsIgnoreCase("[randomtp]")) {
			String noPerms = this.plugin.lang.getConfig().getString("NOPERMISSION").replace("&", "§");
			Player p = e.getPlayer();
			Block b = e.getBlock();
			if (p.hasPermission("randomtp.createsign")) {
				BlockState state = b.getState();
				Sign sign = (Sign) state;
				sign.update();
				
				int econprice = 0;
				int cooldown = 0;
				int blocks = 1000;
				
				String line1 = e.getLine(1); //BLOCKS
				String line2 = e.getLine(2); //PRICE
				String line3 = e.getLine(3); //WORLDNAME
				
				if (!this.plugin.vault || !line3.isEmpty()) {
					econprice = 0;
					p.sendMessage(this.plugin.lang.getConfig().getString("ECONOMYNOTFOUND").replace("&", "§"));
				} else {
					econprice = Integer.valueOf(line3);
				}

				World w = this.plugin.getServer().getWorld(line3);
				
				boolean line1empty = line1.isEmpty();
				boolean line2empty = line2.isEmpty();
				boolean line3empty = line3.isEmpty();
				
				if(!this.plugin.sign.isNumeric(line1)) {
					p.sendMessage(this.plugin.lang.getConfig().getString("NOTVALIDBLOCKNUMBER").replace("&", "§")
							.replace("%variable%", line1));
					e.setCancelled(true);
					b.breakNaturally();
					return;
				} else {
					if(!line1empty) {
						blocks = Integer.valueOf(line1);
					} else {
						p.sendMessage(this.plugin.lang.getConfig().getString("EMPTYBLOCKNUMBER").replace("&", "§"));
						e.setCancelled(true);
						b.breakNaturally();
					}
				}
				if(!this.plugin.sign.isNumeric(line2)) {
					p.sendMessage(this.plugin.lang.getConfig().getString("NOTVALIDPRICENUMBER").replace("&", "§")
							.replace("%variable%", line1));
					e.setCancelled(true);
					b.breakNaturally();
					return;
				} else {
					if(!line2empty) {
						econprice = Integer.valueOf(line2);
					} else {
						p.sendMessage(this.plugin.lang.getConfig().getString("EMPTYPRICENUMBER").replace("&", "§"));
						e.setCancelled(true);
						b.breakNaturally();
					}
				}
				if(w == null) {
					p.sendMessage(this.plugin.lang.getConfig().getString("INVALIDWORLD").replace("&", "§"));
					e.setCancelled(true);
					b.breakNaturally();
					return;
				} else {
					if(line3empty) {
						p.sendMessage(this.plugin.lang.getConfig().getString("NOTVALIDPRICENUMBER").replace("&", "§")
								.replace("%variable%", line1));
						e.setCancelled(true);
						b.breakNaturally();
					}
				}
				
				CustomLoc customLoc = this.plugin.sign.createSign(sign.getLocation(), blocks, econprice, cooldown, w);
				this.plugin.signs.add(customLoc);
				
				ArrayList <String> lines = (ArrayList<String>) this.plugin.getConfig().getStringList("SignFormat");
				
				String distance = "%distance%";
				String price = "%price%";
				if (this.plugin.vault) {
					String stringPrice;
					if(econprice == 0) {
						stringPrice = "";
					} else {
						stringPrice = String.valueOf(econprice);
					}
					e.setLine(0, lines.get(0).replace("&", "§").replace(distance, line1).replace(price, stringPrice));
					e.setLine(1, lines.get(1).replace("&", "§").replace(distance, line1).replace(price, stringPrice));
					e.setLine(2, lines.get(2).replace("&", "§").replace(distance, line1).replace(price, stringPrice));
					e.setLine(3, lines.get(3).replace("&", "§").replace(distance, line1).replace(price, stringPrice));
				} else {
					e.setLine(0, lines.get(0).replace("&", "§").replace(distance, line1).replace(price, ""));
					e.setLine(1, lines.get(1).replace("&", "§").replace(distance, line1).replace(price, ""));
					e.setLine(2, lines.get(2).replace("&", "§").replace(distance, line1).replace(price, ""));
					e.setLine(3, lines.get(3).replace("&", "§").replace(distance, line1).replace(price, ""));
				}
				sign.update();
				this.plugin.sign.reloadSigns();
				this.plugin.help.reload();
				p.sendMessage(this.plugin.lang.getConfig().getString("CREATINGSUCCESS").replace("&", "§"));
			}
			else if (!p.hasPermission("randomtp.createsign")) {
				p.sendMessage(noPerms);
				e.setCancelled(true);
				b.breakNaturally();
			}

		}

	}
	
	@EventHandler
	private void onSignDestroy(BlockBreakEvent e) {
		Block block = e.getBlock();
		if ((block.getState() instanceof Sign)) {
			Sign s = (Sign) block.getState();
			Location loc = s.getLocation();
			Player p = e.getPlayer();
			if(this.plugin.sign.isSign(loc)) {
				if(p.hasPermission("randomtp.breaksigns")) {
					this.plugin.sign.removeSign(loc);
					this.plugin.help.reload();
					p.sendMessage(this.plugin.lang.getConfig()
							.getString("SIGNREMOVED").replace("&", "§"));
				} else {
					p.sendMessage(this.plugin.lang.getConfig()
							.getString("NOPERMISSION").replace("&", "§"));
					e.setCancelled(true);
				}
			}
			
		}
		
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Block clicked = e.getClickedBlock();
		if ((clicked.getState() instanceof Sign)) {
			Sign s = (Sign) clicked.getState();
			this.plugin.sign.prepareForSign(e.getPlayer(), s.getLocation());
		}
	}
	
}

package com.gmail.theposhogamer.Util;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.gmail.theposhogamer.RandomTP;

public class Menu implements Listener {

	private RandomTP plugin;
	
	public Menu(RandomTP plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	public ArrayList<ItemStack> itemstack = new ArrayList <ItemStack> ();

	@SuppressWarnings("deprecation")
	public void registerItems() {

		FileConfiguration cfg = this.plugin.getConfig();
		for (String s: cfg.getConfigurationSection("Inventory.Items").getKeys(false)) {
			String configsec = "Inventory.Items." + s + ".";
			String id = cfg.getString(configsec + "id");
			String name = cfg.getString(configsec + "name").replace("&", "§");
			ArrayList<String> array = (ArrayList<String>) cfg.getStringList(configsec + "lore");
			int distance = cfg.getInt(configsec + "distance");
			int cost = cfg.getInt(configsec + "cost");
			ItemStack slot0 = null;
			if (String.valueOf(id).contains(":")) {
				String a = id;
				String[] arr = a.split(":");
				int finalid = Integer.valueOf(arr[0]);
				int data = Integer.valueOf(arr[1]);
				slot0 = new ItemStack(Material.getMaterial(finalid), 1, (short) data);
				ItemMeta meta0 = slot0.getItemMeta();
				meta0.setDisplayName(name);
				ArrayList < String > arrayconvertion = new ArrayList < String > ();
				for (int i = 0; i < array.size(); i++) {
					arrayconvertion.add(array.get(i).replace("&", "§").replace("%distance%", distance + "").replace("%cost%", cost + ""));
				}
				meta0.setLore(arrayconvertion);
				slot0.setItemMeta(meta0);
				itemstack.add(slot0);
			} else {
				slot0 = new ItemStack(Material.getMaterial(Integer.valueOf(id)), 1);
				ItemMeta meta0 = slot0.getItemMeta();
				meta0.setDisplayName(name);

				ArrayList < String > arrayconvertion = new ArrayList < String > ();
				for (int i = 0; i < array.size(); i++) {
					arrayconvertion.add(array.get(i).replace("&", "§").replace("%distance%", distance + "").replace("%cost%", cost + ""));
				}
				meta0.setLore(arrayconvertion);
				slot0.setItemMeta(meta0);
				itemstack.add(slot0);
			}
		}
	}

	public void openInventory(Player p) {
		FileConfiguration cfg = this.plugin.getConfig();
		int rows = cfg.getInt("Inventory.Rows");
		String menuname = cfg.getString("Inventory.MenuName").replace("&", "§");
		Inventory menu = Bukkit.createInventory(null, 9 * rows, menuname);
		for (String s: cfg.getConfigurationSection("Inventory.Items").getKeys(false)) {
			for(int a = 0; a<itemstack.size(); a++) {
				ItemStack i = itemstack.get(a);
				String configsec = "Inventory.Items." + s + ".";
				int distance = cfg.getInt(configsec + "distance");
				int cost = cfg.getInt(configsec + "cost");
				if (i.getItemMeta().getDisplayName().equalsIgnoreCase(cfg.getString(configsec + "name").replace("&", "§").replace("%distance%", distance + "").replace("%cost%", cost + ""))) {
					int slot = cfg.getInt(configsec + "slot");
					menu.setItem(slot, i);
				}
			}
		}
		p.openInventory(menu);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void OnClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		Inventory in =e.getInventory();
		FileConfiguration cfg = this.plugin.getConfig();
		String noPerms = plugin.lang.getConfig().getString("NOPERMISSION").replace("&", "§");
		String menuname = cfg.getString("Inventory.MenuName").replace("&", "§");
		if (in.getName().equals(menuname)) {
			for (String s: cfg.getConfigurationSection("Inventory.Items").getKeys(false)) {
				String configsec = "Inventory.Items." + s + ".";
				String id = cfg.getString(configsec + "id");
				int cost = cfg.getInt(configsec + "cost");
				String permission = cfg.getString(configsec + "permission");
				String distance = cfg.getString(configsec + "distance");
				String world = cfg.getString(configsec + "world");
				Integer cooldown = cfg.getInt(configsec + "cooldown");
				String name = cfg.getString(configsec + "name").replace("&", "§");
				if (clicked == null) {
					e.setCancelled(true);
					return;
				}
				if (!clicked.getItemMeta().hasDisplayName()) {
					e.setCancelled(true);
					return;
				}
				if(!clicked.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
					e.setCancelled(true);
					return;
				}
				String separator = ":";
				if(id.contains(separator)) {
					String a = id;
					String[] arr = a.split(separator);
					int finalid = Integer.valueOf(arr[0]);
					int data = Integer.valueOf(arr[1]);
					if (clicked.getType().getId() == finalid && data == clicked.getData().getData()) {
						startGUITeleport(p, permission, cooldown, cost, world, distance, noPerms,
								clicked.getItemMeta().getDisplayName());
					}
				} else {
					if (clicked.getType().getId() == Integer.valueOf(id)) {
						startGUITeleport(p, permission, cooldown, cost, world, distance, noPerms,
								clicked.getItemMeta().getDisplayName());
					}
				}
			}
			e.setCancelled(true);
			p.closeInventory();
		}
	}
	
	private void startGUITeleport(Player p, String permission, int cooldown, 
			int cost, String world, String distance, String noPerms, String displayname) {
		
		if (p.hasPermission(permission)) {
			UUID uuid = p.getUniqueId();
			if (cooldown != 0) {
				String joined = displayname+","+uuid.toString();
				Long nowtime = System.currentTimeMillis();
				if(this.plugin.cooldown.containsKey(joined)) {
					Long time = this.plugin.cooldown.get(joined);
					Long elapsedSeconds = (nowtime-time)/1000;
					if(elapsedSeconds >= cooldown) {
						this.plugin.cooldown.remove(joined);
					} else {
						Long left = cooldown-elapsedSeconds;
						p.sendMessage(this.plugin.lang.getConfig().getString("COOLDOWNLEFT").replace("&", "§")
								.replace("%variable%", String.valueOf(left)));
						return;
					}
				} else {
					this.plugin.cooldown.put(joined, nowtime);
				}
			}
			int d = Integer.valueOf(distance);
			if (cost == 0 || cost < 0) {
				this.plugin.sign.initiateTeleport(p, 
						this.plugin.getServer().getWorld(world), d, cost);
			} else {
				if (this.plugin.economy != null) {
					if (this.plugin.economy.getBalance(p) >= cost) {
						this.plugin.economy.withdrawPlayer(p, cost);
						p.sendMessage(this.plugin.lang.getConfig().getString("SUCCESSFULBUY").replace("&", "§")
								.replace("%variable%", String.valueOf(cost)));
						this.plugin.sign.initiateTeleport(p, 
								this.plugin.getServer().getWorld(world), d, cost);
					} else {
						p.sendMessage(this.plugin.lang.getConfig().getString("INSSUFICIENTMONEY").replace("&", "§"));
					}
				} else {
					p.sendMessage("§cAn internal error ocurred, "
							+ "the sign was created with vault economy but "
							+ "now it seems like vault went away");
				}

			}

		} else {
			p.sendMessage(noPerms);
		}
	}
	
}
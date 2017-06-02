package com.gmail.theposhogamer;

import java.util.ArrayList;
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
import org.bukkit.plugin.Plugin;


public class Menu implements Listener {
	
	public Menu() {
		Plugin instance = RandomTP.instance;
		instance.getServer().getPluginManager().registerEvents(this, instance);
	}
	public static ArrayList<ItemStack> itemstack = new ArrayList<ItemStack>();
	
	
	@SuppressWarnings("deprecation")
	public static void registerItems() {
		
		FileConfiguration cfg = RandomTP.instance.getConfig();
		
		for(String s : cfg.getConfigurationSection("Inventory.Items").getKeys(false)) {
			
			String configsec = "Inventory.Items." + s + ".";

			String id = cfg.getString(configsec + "id");
			String name = cfg.getString(configsec + "name").replace("&", "§");
			ArrayList<String> array = (ArrayList<String>) cfg.getStringList(configsec + "lore");


			int distance = cfg.getInt(configsec + "distance");
			int cost = cfg.getInt(configsec + "cost");
			
			ItemStack slot0 = null;
			if(String.valueOf(id).contains(":")) {
				String a = id;
				String[] arr = a.split(":");
				int finalid = Integer.valueOf(arr[0]); 
				int data = Integer.valueOf(arr[1]); 
				slot0 = new ItemStack(Material.getMaterial(finalid), 1, (short) data);
				ItemMeta meta0 = slot0.getItemMeta();
				meta0.setDisplayName(name);
				ArrayList<String> arrayconvertion = new ArrayList<String>();
				for(int i = 0; i<array.size(); i++) {
					arrayconvertion.add(array.get(i).replace("&", "§")
							.replace("%distance%", distance+"")
							.replace("%cost%", cost+""));
				}
				meta0.setLore(arrayconvertion);
				slot0.setItemMeta(meta0);
				itemstack.add(slot0);
			} else {
				slot0 = new ItemStack(Material.getMaterial(Integer.valueOf(id)), 1);
				ItemMeta meta0 = slot0.getItemMeta();
				meta0.setDisplayName(name);

				ArrayList<String> arrayconvertion = new ArrayList<String>();
				for(int i = 0; i<array.size(); i++) {
					arrayconvertion.add(array.get(i).replace("&", "§")
							.replace("%distance%", distance+"")
							.replace("%cost%", cost+""));
				}
				meta0.setLore(arrayconvertion);
				slot0.setItemMeta(meta0);
				itemstack.add(slot0);
			}
			
			
		}
	}
	
	public static void openInventory(Player p) {
		
		FileConfiguration cfg = RandomTP.instance.getConfig();
		
		int rows = cfg.getInt("Inventory.Rows");
		String menuname = cfg.getString("Inventory.MenuName").replace("&", "§");
		Inventory menu = Bukkit.createInventory(null, 9*rows, menuname);
		for(String s : cfg
				.getConfigurationSection("Inventory.Items").getKeys(false)) {
			for(ItemStack i : itemstack) {
				
				String configsec = "Inventory.Items." + s + ".";
				int distance = cfg.getInt(configsec + "distance");
				int cost = cfg.getInt(configsec + "cost");
				if(i.getItemMeta().getDisplayName()
						.equalsIgnoreCase(cfg.getString(configsec +"name").replace("&", "§")
						.replace("%distance%", distance+"").replace("%cost%", cost+""))) {
					int slot = cfg.getInt(configsec + "slot");
					menu.setItem(slot, i);
				}
			}
			
			
		}
		p.openInventory(menu);
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	  public void OnClick(InventoryClickEvent e)
	  {
	    Player p = (Player)e.getWhoClicked();
	    ItemStack clicked = e.getCurrentItem();
	    Inventory in = e.getInventory();

		FileConfiguration cfg = RandomTP.instance.getConfig();
		

		String noPerms = cfg.getString("Messages.NoPermission").replace("&", "§");
		String unable = cfg.getString("Messages.UnableToCreateSign").replace("&", "§");
		
	    String menuname = cfg.getString("Inventory.MenuName").replace("&", "§");
	    if(in.getName().equals(menuname)) {
	    	for(String s : cfg
  				.getConfigurationSection("Inventory.Items").getKeys(false)) {
	    		String configsec = "Inventory.Items." + s + ".";
				String id = cfg.getString(configsec + "id");
				int cost = cfg.getInt(configsec + "cost");
				String permission = cfg.getString(configsec + "permission");
				String distance = cfg.getString(configsec + "distance");
				String world = cfg.getString(configsec + "world");
				String name = cfg.getString(configsec + "name").replace("&", "§");
				if(id.contains(":")) {
  				String a = id;
  				String[] arr = a.split(":");
  				int finalid = Integer.valueOf(arr[0]);
  				int data = Integer.valueOf(arr[1]);
  				if(clicked.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
  					if(clicked.getType().getId() == finalid && data == clicked.getData().getData()) {
  						int price = cost;
  		    			if(p.hasPermission(permission)) {
  		    				if(price == 0 || price < 0) {
  	  		    				RandomTP.initiateTeleport(p,
  		  			    	    			 Bukkit.getWorld(world), Integer.valueOf(distance));
  	  		    			 } else {
  	  		    				if(RandomTP.economy != null) {
  	  		    					RandomTP.pMoney.put(p, price);
  	  	  	  		    			 if(RandomTP.economy.getBalance(p) >= price) {
  	  	  	  		    				 RandomTP.economy.withdrawPlayer(p, price);
  	  	  	  		    				 p.sendMessage(cfg.getString("Messages.Economy").replace("&", "§").replace("%price%", price+""));
  	  	  	  		    				 RandomTP.initiateTeleport(p,
  	  	  	  			    	    			 Bukkit.getWorld(world), Integer.valueOf(distance));
  	  	  	  		    			 } else {
  	  	  	  		    				 p.sendMessage(cfg.getString("Messages.NotEnough").replace("&", "§").replace("%price%", price+""));
  	  	  	  		    			 }
  	  		  		  			} else {
  	  		  		  				p.sendMessage(unable);
  	  		  		  			}
  	  		    				
  	  		    				
  	  		    			 }
  		    			} else {
  		    				p.sendMessage(noPerms);
  		    			}
  					}
  				}} else {
  					if(clicked.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
  	  					if(clicked.getType().getId() == Integer.valueOf(id)) {
  	  					int price = cost;
  		    			if(p.hasPermission(permission)) {
  		    				if(price == 0 || price < 0) {
  	  		    				RandomTP.initiateTeleport(p,
  		  			    	    			 Bukkit.getWorld(world), Integer.valueOf(distance));
  	  		    			 } else {
  	  		    				if(RandomTP.economy != null) {
  	  		    					RandomTP.pMoney.put(p, price);
  	  	  	  		    			 if(RandomTP.economy.getBalance(p) >= price) {
  	  	  	  		    				 RandomTP.economy.withdrawPlayer(p, price);
  	  	  	  		    				 p.sendMessage(RandomTP.instance.getConfig().getString("Messages.Economy").replace("&", "§").replace("%price%", price+""));
  	  	  	  		    				 RandomTP.initiateTeleport(p,
  	  	  	  			    	    			 Bukkit.getWorld(world), Integer.valueOf(distance));
  	  	  	  		    			 } else {
  	  	  	  		    				 p.sendMessage(RandomTP.instance.getConfig().getString("Messages.NotEnough").replace("&", "§").replace("%price%", price+""));
  	  	  	  		    			 }
  	  		  		  			} else {
  	  		  		  				p.sendMessage(unable);
  	  		  		  			}
  	  		    				
  	  		    				
  	  		    			 }
  		    			} else {
  		    				p.sendMessage(noPerms);
  		    			}
  	  					}
  	  				}
  				}
  			}

			e.setCancelled(true);
			p.closeInventory();
	    }
  	} 
	
}

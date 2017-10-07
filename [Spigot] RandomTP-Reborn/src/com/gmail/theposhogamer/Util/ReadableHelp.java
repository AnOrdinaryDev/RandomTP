package com.gmail.theposhogamer.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import com.gmail.theposhogamer.RandomTP;

public class ReadableHelp {
	
	private RandomTP plugin;
	
	public ReadableHelp(RandomTP plugin) {
		this.plugin = plugin;
	}
	
	ArrayList<String> signs = new ArrayList<String>();
	
	public void reload() {
		this.signs.clear();
		for(int i = 0; i<this.plugin.signs.size(); i++) {
			CustomLoc sign = this.plugin.signs.get(i);
			this.signs.add("§c#§f"+(i+1)+" §cLocation(x,y,z,world): §f" +
			sign.x + " " + sign.y + " " + sign.z + " " + sign.world);
		}
	}
	
	public void sendMessage(Player p, int page) {
		int hojas = this.getPages();
		if(page > hojas || page < 1) { page = 1; }
		if(hojas == 0) { page = 0; }
		p.sendMessage("§cSign List (" + page + "/" + hojas + "):");
		for(String sign : this.getPageSigns(p,page,hojas)) {
			p.sendMessage(sign);
		}
	}
	
	public int getPages() {
		double dsize = Double.valueOf(this.signs.size())/5.0;
		if(dsize % 1 != 0) { dsize=dsize+1; }
		int size= (int)dsize;
		return size;
	}
	
	public List<String> getPageSigns(Player p, int page, int pages) {
		List<String> list = new ArrayList<String>();
		if(pages > 0) {
			int totales = pages;
			if (!((pages % 1) == 0)) {
				totales = totales + 1;
			}
			int checker = 0;
			for (int i = 0; i < totales + 1; i++) {
				if (page == i) {
					for (int i2 = ((page * 5)-5); i2 < (page*5); i2++) {
						if(i2<=(this.signs.size()-1)) {
							String sign = signs.get(i2);
							if (sign != null) {
								list.add(sign);
							}
						}
					}
				} else {
					checker = checker + 1;
				}
			}
		}
		return list;
	}
	
}

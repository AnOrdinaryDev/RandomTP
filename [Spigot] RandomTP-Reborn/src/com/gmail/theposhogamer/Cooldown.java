package com.gmail.theposhogamer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class Cooldown {

	public static HashMap<String,String> cooldown = new HashMap<String, String>();
	
	public void load() {
		
		List<String> saved;
		
		FileConfiguration data = RandomTP.data;
		
		if(data.getStringList("Saved") != null) {
			saved = data.getStringList("Saved");
			
			for(int i = 0; i<saved.size(); i++) {
				
				String[] parse = saved.get(i).split(":");
				
				String time = parse[1];
				
				cooldown.put(parse[0], time);
				
			}
		}
		
		System.out.println("[RandomTP-Reborn] All cooldowns had been synchronized");
		
	}
	
	public void save() {
		
		List<String> saved;
		
		List<String> uuidArray = new ArrayList<String>(cooldown.keySet());
		List<String> timeArray = new ArrayList<String>(cooldown.values());
		
		FileConfiguration data = RandomTP.data;
		
		if(data.getStringList("Saved") != null) {
			saved = data.getStringList("Saved");
		} else {
			saved = new ArrayList<String>();
		}
		
		for(int i = 0; i<uuidArray.size(); i++) {
			
			String uuid = uuidArray.get(i);
			
			String time = timeArray.get(i);
			
			String uuidToString = uuid.toString();
			
			String unparsed = uuidToString+":"+time;
			
			saved.add(unparsed);
			
		}
		
		RandomTP.data.set("Saved", saved);
		RandomTP.saveData();
		
		System.out.println("[RandomTP-Reborn] Saving cooldowns");
		
	}
	
	public  void startTask() {
		//Load the saved cooldown players
		load();
		
		//Start task
		new BukkitRunnable() {
			public void run() {
				
				try {
					@SuppressWarnings("unchecked")
					HashMap<String,String> cooldowner = (HashMap<String, String>) cooldown.clone();
					
					for(String uuid : cooldowner.keySet()) {
						
						String timestring = cooldown.get(uuid);
						
						String[] spplited = timestring.split(",");
						
						Integer time = Integer.valueOf(spplited[4]);
						
						if(time > 0) {
							
							time = time-1;

							String x = spplited[0];
							String y = spplited[1];
							String z = spplited[2];
							String world = spplited[3];
							
							String finalstring = x+","+y+","+z+","+world+","+time;
							
							cooldown.put(uuid, finalstring);
							
						} else {
							cooldown.remove(uuid);
						}
						
					}
				}catch(Exception ex) {
					System.out.println("[RandomTP-Reborn] Skipping cooldowner tracker bug...");
					System.out.println("[RandomTP-Reborn] Fixed skipper, working again like a feather");
				}
				
			}
		}.runTaskTimer(RandomTP.instance, 20L, 20L);
		
	}
	
}

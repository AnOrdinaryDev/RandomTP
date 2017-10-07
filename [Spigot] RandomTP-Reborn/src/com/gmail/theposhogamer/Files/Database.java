package com.gmail.theposhogamer.Files;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Database extends YamlConfiguration {
	
	private FileConfiguration data;
	private File dfile;
	
	public FileConfiguration getConfig(){
	    return data;
	}
	
    public Database(Plugin main) {
    	dfile = new File(main.getDataFolder(), "database.yml");
    	if (!dfile.exists()) {
    		try {
    			dfile.createNewFile();
    		}
    		catch(Exception ex) {
    			System.out.println("[RandomTP] An exception"
    					+ " ocurred while trying to create a new database yml file");
    		}
    	}
    	data = YamlConfiguration.loadConfiguration(dfile);
    }
    
    public void reload() {
    	data = YamlConfiguration.loadConfiguration(dfile);
    }
    
    public void save() {
    	try { data.save(dfile); } catch (Exception ex) {
    		System.out.println("[RandomTP] An exception"
					+ " ocurred while trying save the database yml file");
    	}
    }
    
}
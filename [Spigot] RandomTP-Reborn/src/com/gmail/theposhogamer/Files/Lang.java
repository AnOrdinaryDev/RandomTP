package com.gmail.theposhogamer.Files;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.gmail.theposhogamer.RandomTP;

public class Lang extends YamlConfiguration {
	
	private FileConfiguration data;
	private File dfile;
	
	public FileConfiguration getConfig(){
	    return data;
	}
	
    public Lang(RandomTP main) {
    	dfile = new File(main.getDataFolder(), "lang.yml");
    	if (!dfile.exists()) {
    		try {
    			dfile.createNewFile();
    		}
    		catch(Exception ex) {
    			main.getServer().getConsoleSender().sendMessage(main.prefix + "An exception ocurred"
    					+ " while trying to create a new lang yml file");
    		}
    	}
    	data = YamlConfiguration.loadConfiguration(dfile);
    	data.options().copyDefaults(true);
    	data.addDefault("NOPERMISSION", "&cYou do not have permission to perform this action");
    	data.addDefault("ECONOMYNOTFOUND", "&cNo economy plugin was detected, so price was put to 0");
    	data.addDefault("NOTVALIDBLOCKNUMBER", "&c%variable% is not a valid number of distance");
    	data.addDefault("EMPTYBLOCKNUMBER", "&cDistance cannot be empty");
    	data.addDefault("NOTVALIDPRICENUMBER", "&c%variable% is not a valid price");
    	data.addDefault("EMPTYPRICENUMBER", "&cDistance cannot be empty");
    	data.addDefault("INVALIDWORLD", "&cThat world does not exist");
    	data.addDefault("UNEXISTENT", "&cInvalid world, cannot teleport randomly");
    	data.addDefault("CREATINGSUCCESS", "&aSign was successfully created");
    	data.addDefault("SIGNREMOVED", "&cSign successfully was removed");
    	data.addDefault("SUCCESSTELEPORT", "&aYou have been teleported to a random location");
    	data.addDefault("UNSUCCESSFULTELEPORT", "&aNo location found, try again please");
    	data.addDefault("INSSUFICIENTMONEY", "&cYou don't have enough money to buy this teleport, you need $%variable% to buy this");
    	data.addDefault("SUCCESSFULBUY", "&aYou have afforded $%variable% to buy this random teleport");
    	data.addDefault("COOLDOWNLEFT", "&6You need to wait %variable% second/s before using this again");
    	data.addDefault("CONSOLEINVALIDBLOCKSNUMBER", "&cInvalid blocks distance, have you written a number?");
    	save();
    }
    
    public void reload() {
    	data = YamlConfiguration.loadConfiguration(dfile);
    }
    
    public void save() {
    	try { data.save(dfile); } catch (Exception ex) {
    		System.out.println("[RandomTP] An exception"
					+ " ocurred while trying save the lang yml file");
    	}
    }
    
}
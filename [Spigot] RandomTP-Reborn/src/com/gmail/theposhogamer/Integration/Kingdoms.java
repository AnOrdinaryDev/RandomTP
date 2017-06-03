package com.gmail.theposhogamer.Integration;

import org.kingdoms.manager.game.GameManagement;

public class Kingdoms {
	
	private static GameManagement kingdomsmanager;
	
	public GameManagement getManager() {
		return kingdomsmanager;
	}
	
	public void setManager(GameManagement manager) {
		kingdomsmanager = manager;
	}
	
}

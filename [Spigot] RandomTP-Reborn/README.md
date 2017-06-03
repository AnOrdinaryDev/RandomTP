# RandomTP

> RandomTP (also known as Random Teleport is a minecraft plugin, the object of which is to teleport people to Random Locations of the world.  
\- Plugin Link: http://spigotmc.org/resources/5084/

<br />I made a RandomRP plugin for Minecraft that does exactly this. Players can use GUI menus with cooldowns, random teleport sign and use them to teleport themselves to a random location in a world.
<br /><br />
## Features
\- Fully configurable 
\- No black hole after teleport, in normal plugins, after the teleport you see a black hole for seconds, and here is reduced.  
\- No glitch, in some plugins when you get teleported, you die or you get glitched on a block , this plugin contains a block glitch detector, to detect if someone is being glitched and unglitch them.  
\-No teleport to seas or oceans, and you won't fall never at lava
\- Lots of support for other plugins   
\- And lots more  
<br />
## Documentation
A lot of the plugin is documented here:  
http://spigotmc.org/resources/5084/
<br /><br />
## Current supported plugins
#### Group plugins:
\- Factions  
\- FactionsUUID  
\- PreciousStones 
\- Kingdoms  
  
Is the group plugin you use on your server not supported?   
Don't hesitate to ask me to add support for it :)


#### Other supported plugins:
\- Vault (To put price and charge for the teleport cost)

Same rule applies, want support for a plugin?  
Don't hesitate to ask :)<br /><br />  

### Dependencies:
Although my plugin is completely modular in a way that dependencies aren't intertwined with the rest of the code. Most of the time, removing a dependency is as simple as removing its class and 1 line somewhere else in the code. But with some dependencies this is not the case. Those are listed here:

#### Java 1.8+
To use the Java Lambda feature, Java 1.8 or higher is required.

#### Spigot 1.7 to 1.12
The plugin actually is compatible with all the spigot versions, if it doesn't work for any of them report it and I'll be really happy to fix it.

### Current (soft and hard) dependencies used in the project
Here is a list of all the current dependencies used in KoTH:  
\- Factions  
\- FactionsUUID 
\- PreciousStones  
\- GLib (As a dependency of Kingdoms)  
\- Kingdoms 
\- Vault

### Current state of the code
The code might be badly commented, but whenever I write new code now I try to comment it so it is easily readable for everyone else.  
  
At this point this plugin is still pretty much a one-person project. Which means only 1 person is looking at the code. This means that there are only 1 kind of eyes looking at a certain problem, which can lead in maybe not-so efficient code. If you think something can be improved, don't hesitate to help me ofcourse! ^^<br />  

### Contributing
If you want to contribute something to the project start with forking the project. 

Make the additions/changes you need and make a commit. Make sure you only commit the code with the needed additions. Make sure you don't commit the code that you removed for removing a dependency.  
  
And finally, make a pull-request preferably to the **Development** branch. Master is only pushed to for releases.  
<br /><br />
## For developers
If you want to add support to RandomTP in some way with a custom plugin, you can use the API to do so. Here are some examples on how you would use the API.

### Using the Plugin API
```java
Plugin plugin = getServer().getPluginManager().getPlugin("RandomTP");
if (plugin == null) {
  System.out.println("RandomTP plugin not found!");
}

// Then to use the API you only need to call this class "RandomTPAPI" Example:

@EventHandler
public void onJoinEvent(PlayerJoinEvent e) {
  Player p = e.getPlayer();
  //RandomTPAPI.teleportRandom(player,world, blocksDistance)
  RandomTPAPI.teleportRandom(p, p.getWorld(), 100);
}
```

<br /><br />
## Copyright
This project is not under any licence, free use.
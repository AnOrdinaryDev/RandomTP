package com.gmail.theposhogamer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Updater {

	public void checkUpdate(Plugin instance) {

		Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
			@Override
			public void run() {
				System.out.println("[RandomTP-Reborn] Checking for updates...");
				try {
					HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
					c.setDoOutput(true);
					c.setRequestMethod("POST");
					c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=5084").getBytes("UTF-8"));
					String oldVersion = instance.getDescription().getVersion();
					String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine();

					int cases = versionComparator(oldVersion, newVersion);

					if (cases == 0) {
						System.out.println("[RandomTP-Reborn] You are running the newest stable build.");
					} else if (cases == 1) {
						System.out.println("[RandomTP-Reborn] Your version is newer than the last stable build, maybe running an snapshot.");
					} else if (cases == -1) {
						System.out.println("[RandomTP-Reborn] New stable version availiable! Download it at: http://www.spigotmc.org/resources/5084/.");
					} else {
						System.out.println("[RandomTP-Reborn] Can't compare versions, it's a little bit difficult to compare unreadeable version names.");
					}
				}
				catch(Exception e) {

					System.out.println("[RandomTP-Reborn] No update data found, maybe you don't have internet connection.");

				}
			}
		},
		100L);

	}
	
	public int versionComparator(String version_1, String version_2) {
		String[] array_1 = version_1.split("\\.");

		String[] array_2 = version_2.split("\\.");

		int i = 0;
		while ((i < array_1.length) && (i < array_2.length) && (array_1[i].equals(array_2[i]))) {
			i++;
		}
		if ((i < array_1.length) && (i < array_2.length)) {
			int j = Integer.valueOf(array_1[i]).compareTo(Integer.valueOf(array_2[i]));

			return Integer.signum(j);
		}
		return Integer.signum(array_1.length - array_2.length);
	}

}
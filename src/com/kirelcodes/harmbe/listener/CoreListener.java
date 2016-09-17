package com.kirelcodes.harmbe.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class CoreListener implements Listener {
	public CoreListener(Plugin plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
}

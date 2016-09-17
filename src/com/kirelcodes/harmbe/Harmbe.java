package com.kirelcodes.harmbe;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.kirelcodes.harmbe.pathfinders.HarmbePassivePathfinder;
import com.kirelcodes.miniaturepets.api.APIUtils;
import com.kirelcodes.miniaturepets.api.pets.APIMob;
import com.kirelcodes.miniaturepets.api.pets.APIMobContainer;
import com.kirelcodes.miniaturepets.api.pets.MobSpawnAction;

public class Harmbe extends JavaPlugin {
	private static APIMobContainer harmbe = null;
	private static Harmbe instance = null;

	@Override
	public void onEnable() {
		setupInstance();
		if(!setupHarmbe()){
			getLogger().warning("Couldn't setup harmbe container");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	}

	private void setupInstance(){
		instance = this;
	}
	
	private boolean setupHarmbe(){
		File model = null;
		try{
			model = APIUtils.loadModelByName("Gorilla", getInstance());
		}catch(Exception e){
			return false;
		}
		harmbe = new APIMobContainer(model, "HarmbeMob", 35, 0.17D, EntityType.SPIDER, "Spider");
		harmbe.addSpawnAction(new MobSpawnAction() {
			
			@Override
			public void spawnMob(APIMob mob, Location loc) {
				try{
					mob.clearAI();
				}catch(Exception e){
					getLogger().warning(
							"Couldnt intsilaize the milk man disabling the plugin");
					Bukkit.getPluginManager().disablePlugin(getInstance());
					return;
				}
				mob.addObject("passiveHarmbe", true);
				mob.addObject("targetHarmbe", null);
				mob.setCustomName("Harmbe");
				mob.getPathManager().addPathfinder(new HarmbePassivePathfinder(mob));
			}
		});
		return true;
	}
	
	public static APIMobContainer getHarmbe() {
		return harmbe;
	}

	public static Harmbe getInstance() {
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		if (!label.equalsIgnoreCase("spawnHarmbe"))
			return false;
		if(sender.hasPermission("Harmbe.spawnHarmbe"))
		getHarmbe().spawnMob(((Player) sender).getLocation());
		return false;
	}


}

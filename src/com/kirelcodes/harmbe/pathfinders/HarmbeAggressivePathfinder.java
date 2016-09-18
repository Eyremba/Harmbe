package com.kirelcodes.harmbe.pathfinders;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import com.kirelcodes.harmbe.Harmbe;
import com.kirelcodes.miniaturepets.api.pathfinding.Pathfinder;
import com.kirelcodes.miniaturepets.api.pets.APIMob;

public class HarmbeAggressivePathfinder extends Pathfinder {
	private APIMob mob = null;
	private Player targetPlayer;
	private int cooldown = -1;
	public HarmbeAggressivePathfinder(APIMob mob) {
		if (!"HarmbeMob".equalsIgnoreCase(mob.getName())) {
			Harmbe.getInstance()
					.getLogger()
					.warning(
							"Someone is trying to use a milk man pathfinder on a non milkman mob");
			return;
		}
		this.mob = mob;
	}
	
	@Override
	public void onStart() {
		targetPlayer = (Player) mob.getObject("targetHarmbe");
		cooldown = -1;
	}
	
	@Override
	public boolean shouldStart() {
		if (mob != null) {
			return !isHarmbePassive();
		} else
			return false;
	}

	@Override
	public boolean keepWorking() {
		return shouldStart() && targetPlayer != null && mob.getObject("targetHarmbe") != null;
	}
	
	public void harmbeSpeakNearBy(String harmbeTalk, int rad) {
		for (Entity e : mob.getNavigator().getNearbyEntities(rad, rad, rad)) {
			if (!(e instanceof Player))
				continue;
			Player p = (Player) e;
			p.sendMessage("§c<Harambe>§3 " + harmbeTalk);
		}
	}
	@Override
	public void afterTask() {
		if (onCooldown())
			cooldown--;
	}
	@Override
	public void updateTask() {
		if(!handlePlayer())
			return;
		if(!hasTargetLocation()){
			try{
				mob.setTargetLocation(targetPlayer.getLocation());
			}catch(Exception e){
				e.printStackTrace();
			}
			return;
		}
		if(onTargetLocation()){
			if(onCooldown())
				return;
			Fireball ball = (Fireball) mob.getLocation().getWorld().spawnEntity(mob.getLocation().add(mob.getLocation().getDirection().multiply(1.2)), EntityType.FIREBALL);
			ball.setShooter(mob.getNavigator());
			ball.setBounce(true);
			ball.setVelocity(ball.getLocation().toVector().subtract(targetPlayer.getLocation().toVector()));
			harmbeSpeakNearBy("HARMBE BLOW ! HARMBE MAD", 10);
			startCooldown();
			return;
		}
	}

	
	
	public boolean isHarmbePassive() {
		return (boolean) mob.getObject("passiveHarmbe");
	}
	
	private boolean hasTargetLocation() {
		if (mob.getTargetLocation() != null) {
			return mob.getTargetLocation().distanceSquared(
					targetPlayer.getLocation()) < 64;
		} else
			return false;
	}

	private boolean onTargetLocation() {
		return mob.getLocation().distanceSquared(targetPlayer.getLocation()) < 64;
	}
	
	private boolean handlePlayer(){
		if(targetPlayer == null || targetPlayer.isDead() || targetPlayer.getLocation().distanceSquared(mob.getLocation()) > 225){
			mob.updateObject("passiveHarmbe", true);
			mob.updateObject("targetHarmbe", null);
			return false;
		}
		return true;
	}
	private boolean onCooldown() {
		return cooldown > -1;
	}

	private void startCooldown() {
		cooldown = 5 * 20;
	}
}

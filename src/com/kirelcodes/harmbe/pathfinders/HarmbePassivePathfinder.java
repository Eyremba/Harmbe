package com.kirelcodes.harmbe.pathfinders;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kirelcodes.harmbe.Harmbe;
import com.kirelcodes.harmbe.utils.NMSClassInteracter;
import com.kirelcodes.miniaturepets.api.pathfinding.Pathfinder;
import com.kirelcodes.miniaturepets.api.pets.APIMob;

public class HarmbePassivePathfinder extends Pathfinder {

	private APIMob mob = null;
	private Player targetPlayer;
	private int cooldown = -1;

	public HarmbePassivePathfinder(APIMob mob) {
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
		targetPlayer = null;
	}

	@Override
	public boolean shouldStart() {
		if (mob != null) {
			return isHarmbePassive();
		} else
			return false;
	}

	@Override
	public void afterTask() {
		if (onCooldown())
			cooldown--;
	}

	public boolean isHarmbePassive() {
		return (boolean) mob.getObject("passiveHarmbe");
	}

	private boolean hasTargetPlayer() {
		return targetPlayer != null;
	}

	public void harmbeSpeakNearBy(String harmbeTalk, int rad) {
		for (Entity e : mob.getNavigator().getNearbyEntities(rad, rad, rad)) {
			if (!(e instanceof Player))
				continue;
			Player p = (Player) e;
			p.sendMessage("§c<Harambe>§3 " + harmbeTalk);
		}
	}

	public void harmbeSpeak(String harmbeTalk) {
		Bukkit.broadcastMessage("§c<Harambe>§3 " + harmbeTalk);
	}

	@Override
	public void updateTask() {
		if (onCooldown())
			return;
		if (!hasTargetPlayer()) {
			stopPathFinding();
			if (searchPlayer())
				return;
			return;
		}
		if (!hasTargetLocation()) {
			try {
				if (!mob.setTargetLocation(targetPlayer.getLocation())) {
					this.targetPlayer = null;
					stopPathFinding();
				} else
					harmbeSpeakNearBy(targetPlayer.getDisplayName()
							+ "§3 waitttttttt dont move stay where you are !",
							40);
			} catch (Exception e) {
				this.targetPlayer = null;
				stopPathFinding();
			}
			return;
		}
		if (onTargetLocation()) {
			stopPathFinding();
			String message = generateRandomMessage();
			if (targetPlayer.isOp())
				harmbeSpeak(message);
			else
				harmbeSpeakNearBy(message, 100);
			Material[] drops = { Material.YELLOW_FLOWER,
					Material.POISONOUS_POTATO, Material.APPLE, Material.TORCH,
					Material.REDSTONE_TORCH_OFF, Material.LEATHER_HELMET,
					Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS,
					Material.LEATHER_BOOTS, Material.LEATHER, Material.LEASH };
			Random rand = new Random();
			targetPlayer
					.getLocation()
					.getWorld()
					.dropItemNaturally(targetPlayer.getLocation(),
							new ItemStack(drops[rand.nextInt(drops.length)]));
			this.targetPlayer = null;
			startCooldown();
		}
	}

	private boolean searchPlayer() {
		for (Entity e : mob.getNavigator().getNearbyEntities(20 , 100 , 20)) {
			if (!e.isOnGround())
				continue;
			if (!(e instanceof Player))
				continue;
			targetPlayer = (Player) e;
			try{
			if (!mob.setTargetLocation(targetPlayer.getLocation())) {
				this.targetPlayer = null;
				stopPathFinding();
			} else
				harmbeSpeakNearBy(targetPlayer.getDisplayName()
						+ "§3 waitttttttt dont move stay where you are !",
						40);
			}catch(Exception exc){
				exc.printStackTrace();
			}
			return true;
		}
		return false;
	}

	private void stopPathFinding() {
		try {
			mob.stopPathfinding();
			NMSClassInteracter.setDeclaredField(mob, "targetLocation", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean hasTargetLocation() {
		if (mob.getTargetLocation() != null) {
			return mob.getTargetLocation().distanceSquared(
					targetPlayer.getLocation()) < 16;
		} else
			return false;
	}

	private boolean onTargetLocation() {
		return mob.getLocation().distanceSquared(targetPlayer.getLocation()) < 9;
	}

	private String generateRandomMessage() {
		String playerName = targetPlayer.getDisplayName();
		String[] message = {
				String.format("OMG %s§3 is so much amazing", playerName),
				String.format("DONT SHOOT %s§3 Im trying to help !", playerName),
				String.format("%s§3 if you ever need a babysitter tell me",
						playerName),
				String.format("HARMBE Babysitting cheap reliable baby sitter",
						playerName),
				String.format("Oa Oa %s§3 e e e Oa Oa Oa", playerName),
				String.format("Give me banana ! %s§3 BANANA !", playerName),
				String.format("Give me the child ! %s§3 Don't hurt him !",
						playerName), String.format("*DEAD*", playerName),
				String.format("#BanFireArms %s§3", playerName),
				String.format("Baby dont hurt dont hurt me no more", playerName)};
		Random rand = new Random();
		return message[rand.nextInt(message.length)];
	}

	private boolean onCooldown() {
		return cooldown > -1;
	}

	private void startCooldown() {
		cooldown = 20 * 20;
	}

}

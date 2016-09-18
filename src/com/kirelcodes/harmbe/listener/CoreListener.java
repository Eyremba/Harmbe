package com.kirelcodes.harmbe.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.kirelcodes.harmbe.Harmbe;
import com.kirelcodes.harmbe.utils.ItemStackUtils;
import com.kirelcodes.miniaturepets.api.events.MobDamagedByEntityEvent;
import com.kirelcodes.miniaturepets.api.events.MobDeathEvent;

@SuppressWarnings("deprecation")
public class CoreListener implements Listener {
	public CoreListener(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void harmbeDeathEvent(MobDeathEvent e) {
		if(!"HarmbeMob".equalsIgnoreCase(e.getMob().getName()))
			return;
		e.getDrops().clear();
		e.setDroppedExp(1000);
		try {
			ItemStack harmbeSkull = ItemStackUtils
					.getSkullFromURL(
							"http://textures.minecraft.net/texture/46f0b9cea78bcab7be7d119887c53c7aeb0b282d1f2c9f7bff6f7d392259cc",
							"Harambe");
			e.getDrops().add(harmbeSkull);
		} catch (Exception e1) {
		}
		e.getDrops().add(
				ItemStackUtils.createItem(Material.DIAMOND_SWORD,
						"&cBloddy Diamond Sword", ChatColor.RED
								+ "Used to kill harambe", ChatColor.RED
								+ "Shame on you"));
		if (e.getMob().getNavigator().getKiller() != null) {
			e.getMob().getNavigator().getKiller().sendMessage(ChatColor.RED + "You have been cursed by harambe.");
			new BukkitRunnable() {
				private Player target = e.getMob().getNavigator().getKiller();
				private int times = 0;

				@Override
				public void run() {
					if (times > 5) {
						cancel();
						return;
					}
					if (target == null || !target.isOnline()) {
						cancel();
						return;
					}
					Random rand = new Random();
					target.sendMessage(ChatColor.RED + "You have been cursed by harambe.");
					switch (rand.nextInt(9)) {
					case 0:
						target.getWorld().strikeLightning(target.getLocation());
						target.sendMessage(ChatColor.RED
								+ "Harambe is after you");
						break;
					case 1:
						for (int i = 0; i < 5; i++)
							target.getWorld().spawnEntity(target.getLocation(),
									EntityType.CREEPER);
						target.sendMessage(ChatColor.RED
								+ "You would soon see Harambe in person");
						break;
					case 2:
						target.setFoodLevel(0);
						target.setLevel(0);
						target.setVelocity(new Vector(0, 5, 0));
						target.sendMessage(ChatColor.RED
								+ "Harambe is calling you");
						break;
					case 3:
						target.addPotionEffect(new PotionEffect(
								PotionEffectType.POISON, 1000, 2));
						target.sendMessage(ChatColor.RED
								+ "Poisoned by Harambe");
						break;
					case 4:
						target.setPlayerWeather(WeatherType.DOWNFALL);
						target.setPlayerTime(18000L, false);
						target.sendMessage(ChatColor.RED + "Bad luck ?");
						break;
					case 5:
						for (int i = 0; i < 15; i++)
							target.getLocation().clone().add(0, 10 + i, 0).getBlock().setType(Material.ANVIL);;
						target.sendMessage(ChatColor.RED
								+ "Dont you hate when anvils just fall ?");
						break;
					case 6:
						target.launchProjectile(Arrow.class, target.getLocation().clone().add(0, 100, 0).toVector().subtract(target.getLocation().toVector())).setPassenger(target);
						target.sendMessage(ChatColor.RED + "Enjoy the flight you might just see Harambe");
						break;
					case 7:
						target.chat("I killed harambe");
						target.chat("I'm such a noob");
						target.chat(String.format("You should come kill meh I'm at X : %d Y : %d Z : %d" , target.getLocation().getBlockX() , target.getLocation().getBlockY() , target.getLocation().getBlockZ()));
						break;
					case 8:
						target.damage(10);
						target.sendMessage(ChatColor.RED + "Harambe likes to damage you");
						break;
					}

					times++;
				}
			}.runTaskTimer(Harmbe.getInstance(), 120 * 20L, 120 * 20L);
		}
	}
	@EventHandler
	public void harambeTurnAggresive(MobDamagedByEntityEvent e){
		if(!(e.getDamager() instanceof Player))
			return;
		if(!"HarmbeMob".equalsIgnoreCase(e.getMob().getName()))
			return;
		e.getMob().updateObject("passiveHarmbe", false);
		e.getMob().updateObject("targetHarmbe", e.getDamager());
	}
	@EventHandler
	public void harambeBlowHisAssOff(MobDamagedByEntityEvent e){
		if(!"HarmbeMob".equalsIgnoreCase(e.getMob().getName()))
			return;
		if(!(e.getDamager() instanceof Fireball))
			return;
		if(((boolean)e.getMob().getObject("passiveHarmbe")))
			return;
		e.setCancelled(true);
	}
	@EventHandler
	public void harambeSpawning(PlayerChatEvent e){
		String message = e.getMessage();
		if(!message.toLowerCase().contains("harambe"))
			return;
		Random rand = new Random();
		if(rand.nextInt(100) != 99)
			return;
		Harmbe.getHarmbe().spawnMob(e.getPlayer().getLocation().add(rand.nextInt(5), 0, rand.nextInt(5)));
		Bukkit.broadcastMessage(ChatColor.RED + "Harambe have came down from heaven to bless our with his awesomeness");
	}
}

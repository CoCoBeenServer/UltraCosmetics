package be.isach.ultracosmetics.v1_8_R1.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.v1_8_R1.customentities.CustomEntityFirework;
import be.isach.ultracosmetics.v1_8_R1.customentities.CustomGuardian;
import net.minecraft.server.v1_8_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RadBuilder
 */
public class MorphElderGuardian extends Morph {
	
	private boolean cooldown;
	private CustomGuardian customGuardian;
	
	/**
	 * List of the custom entities.
	 */
	public static List<net.minecraft.server.v1_8_R1.Entity> customEntities = new ArrayList<>();
	
	public MorphElderGuardian(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MorphType.valueOf("elderguardian"), ultraCosmetics);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ((event.getAction() == Action.LEFT_CLICK_AIR
		     || event.getAction() == Action.LEFT_CLICK_BLOCK) && !cooldown
		    && event.getPlayer() == getPlayer()) {
			shootLaser();
			cooldown = true;
			Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> cooldown = false, 80);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (((CraftEntity) event.getDamager()).getHandle() == customGuardian
		    && event.getEntity() == getPlayer())
			event.setCancelled(true);
	}
	
	private void shootLaser() {
		if (customGuardian == null)
			return;
		
		final Location FROM = customGuardian.getBukkitEntity().getLocation();
		final Location TO = FROM.clone().add(getPlayer().getLocation().getDirection().multiply(10));
		
		final ArmorStand armorStand = getPlayer().getWorld().spawn(TO, ArmorStand.class);
		
		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setSmall(true);
		
		customGuardian.target(armorStand);
		
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			FireworkEffect.Builder builder = FireworkEffect.builder();
			FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE)
			                               .withColor(Color.TEAL).withFade(Color.TEAL).build();
			
			CustomEntityFirework.spawn(TO, effect);
			
			Vector vector = TO.toVector().subtract(FROM.toVector());
			
			Location current = FROM.clone();
			
			for (int i = 0; i < 10; i++) {
				for (Entity entity : armorStand.getNearbyEntities(4.5, 4.5, 4.5))
					if (entity instanceof LivingEntity
					    && entity != getPlayer())
						MathUtils.applyVelocity(entity, new Vector(0, 0.5d, 0));
				current.add(vector);
			}
			armorStand.remove();
			customGuardian.target(null);
		}, 25);
	}
	
	@Override
	protected void onEquip() {
		super.onEquip();
		
		World world = ((CraftWorld) getPlayer().getWorld()).getHandle();
		
		customGuardian = new CustomGuardian(world);
		customEntities.add(customGuardian);
		customGuardian.check();
		
		Location location = getPlayer().getLocation();
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		customGuardian.setLocation(x, y, z, 0, 0);
		
		EntitySpawningManager.setBypass(true);
		world.addEntity(customGuardian);
		EntitySpawningManager.setBypass(false);
		
		getPlayer().setPassenger(customGuardian.getBukkitEntity());
		
		customGuardian.setInvisible(true);
	}
	
	@Override
	public void onClear() {
		if (customGuardian != null)
			customGuardian.dead = true;
		customEntities.remove(customGuardian);
	}
	
	@Override
	public void onUpdate() {
		if (getOwner() == null
		    || getPlayer() == null) {
			cancel();
			return;
		}
		if (customGuardian == null
		    || !customGuardian.isAlive()) {
			getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).removeMorph();
			cancel();
		}
	}
}

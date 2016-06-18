package be.isach.ultracosmetics.v1_9_R1;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.v1_9_R1.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_9_R1.customentities.CustomEntityRabbit;
import be.isach.ultracosmetics.v1_9_R1.customentities.RideableSpider;
import be.isach.ultracosmetics.version.IModule;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class Module implements IModule {
    @Override
    public void enable() {
        CustomEntities.registerEntities();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("a");
                Player pl = Bukkit.getPlayer("iSach");
                RideableSpider rabbit = new RideableSpider(((CraftWorld)pl.getWorld()).getHandle());
                Location l = pl.getLocation();
                rabbit.setLocation(l.getX(), l.getBlockY(), l.getZ(), 0f, 0f);
                ((CraftWorld)pl.getWorld()).getHandle().addEntity(rabbit);
                rabbit.getBukkitEntity().setPassenger(pl);
            }
        };
//        runnable.runTaskLater(UltraCosmetics.getInstance(), 100);
    }

    @Override
    public void disable() {
        CustomEntities.unregisterEntities();
    }
}

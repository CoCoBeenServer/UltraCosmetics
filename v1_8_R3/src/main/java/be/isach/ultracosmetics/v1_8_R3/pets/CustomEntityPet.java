package be.isach.ultracosmetics.v1_8_R3.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.v1_8_R3.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_8_R3.customentities.Pumpling;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Sacha on 7/03/16.
 */
public abstract class CustomEntityPet extends Pet {

    /**
     * Custom Entity.
     */
    public Entity customEntity;

    public CustomEntityPet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType, ItemStack dropItem) {
        super(owner, ultraCosmetics, petType, dropItem);

    }

    @Override
    public void onEquip() {
        followTask = new PlayerFollower(this, getPlayer());
        if (getOwner().getCurrentPet() != null)
            getOwner().removePet();

        getOwner().setCurrentPet(this);

        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();

        if (this instanceof PetPumpling) {
            customEntity = new Pumpling(((CraftPlayer) getPlayer()).getHandle().getWorld(), getOwner().getBukkitPlayer()).getBukkitEntity();
        }
        CustomEntities.addCustomEntity(getCustomEntity());
        getCustomEntity().setLocation(x, y, z, 0, 0);
        Location spawnLoc = customEntity.getLocation();
        armorStand = (ArmorStand) customEntity.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setCustomNameVisible(true);
        FixedMetadataValue metadataValue = new FixedMetadataValue(getUltraCosmetics(), "C_AD_ArmorStand");
        armorStand.setMetadata("C_AD_ArmorStand", metadataValue);
        updateName();

        customEntity.setPassenger(armorStand);
        EntitySpawningManager.setBypass(true);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getCustomEntity());
        EntitySpawningManager.setBypass(false);
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().dead = true;
        CustomEntities.removeCustomEntity(getCustomEntity());
    }

    @Override
    public boolean isCustomEntity() {
        return true;
    }

    @Override
    public Entity getEntity() {
        return customEntity;
    }

    public net.minecraft.server.v1_8_R3.Entity getCustomEntity() {
        return ((CraftEntity) customEntity).getHandle();
    }
}

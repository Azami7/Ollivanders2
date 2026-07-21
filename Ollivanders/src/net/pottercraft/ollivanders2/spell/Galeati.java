package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base class for spells that replace a target player's helmet with a block.
 * <p>
 * In Harry Potter lore these are Transfiguration spells, but they behave like charm projectiles here and set their
 * branch to {@link net.pottercraft.ollivanders2.O2MagicBranch#CHARMS}.
 * </p>
 *
 * @see MELOFORS
 * @see HERBIFORS
 */
public abstract class Galeati extends O2Spell {
    /**
     * The material placed on the target player's head. Subclasses set this in their casting constructor.
     */
    protected Material helmetType = Material.AIR;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Galeati(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public Galeati(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Replace the first nearby player's helmet (excluding the caster) with {@link #helmetType}, dropping their
     * existing helmet on the ground, then end the spell. Killed if the projectile hits a block first.
     */
    @Override
    protected void doCheckEffect() {
        // projectile has stopped, kill the spell
        if (hasHitBlock())
            kill();

        List<Player> livingEntities = getNearbyPlayers(defaultRadius);

        for (Player target : livingEntities) {
            if (target.getUniqueId().equals(caster.getUniqueId()))
                continue;

            EntityEquipment entityEquipment = target.getEquipment();
            if (entityEquipment == null) {
                continue;
            }

            ItemStack helmet = entityEquipment.getHelmet();
            if (helmet != null) {
                if (helmet.getType() != Material.AIR)
                    target.getWorld().dropItem(target.getEyeLocation(), helmet);
            }

            entityEquipment.setHelmet(new ItemStack(helmetType, 1));
            kill();
            return;
        }
    }

    /**
     * Get the material type that this spell places on the target's head.
     *
     * @return the helmet material
     */
    public Material getHelmetType() {
        return helmetType;
    }
}

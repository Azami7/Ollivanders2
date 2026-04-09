package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstract base class for spells that replace a player's helmet with a specific block type.
 * <p>
 * Galeati spells use the entity-scanning projectile model: each tick the projectile scans for
 * nearby players within {@link #defaultRadius}. The first non-caster player found has their
 * current helmet dropped at their eye location (if non-AIR) and replaced with a new
 * {@link ItemStack} of {@link #helmetType}. Subclasses set {@code helmetType} in their
 * casting constructor to control what block the target wears.
 * </p>
 * <p>
 * In Harry Potter lore these would be Transfiguration spells, but for code purposes they
 * behave like charm projectiles and set {@code branch} to {@link net.pottercraft.ollivanders2.O2MagicBranch#CHARMS}.
 * </p>
 *
 * @see MELOFORS
 * @see HERBIFORS
 */
public abstract class Galeati extends O2Spell {
    /**
     * The material placed on the target player's head. Defaults to {@link Material#AIR};
     * subclasses override this in their casting constructor.
     */
    protected Material helmetType = Material.AIR;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Galeati(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public Galeati(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Scan for a nearby player and replace their helmet with {@link #helmetType}.
     * <p>
     * Each tick, the method scans within {@link #defaultRadius} for players. The caster is
     * skipped. The first non-caster player found has their current helmet (if any and non-AIR)
     * dropped as an item at their eye location, then replaced with a new item of
     * {@code helmetType}. The spell is then killed (single-target).
     * </p>
     * <p>
     * If the projectile hits a block before finding a player, the spell is killed and returns
     * silently.
     * </p>
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

package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Aqua Eructo Duo — an enhanced water-based spell that damages fire-based mobs.
 *
 * <p>AQUA_ERUCTO_DUO is a variant of AQUA_ERUCTO that targets fire-based entities (such as Blazes
 * and Magma Cubes) rather than burning entities. Instead of extinguishing fire, this spell shoots
 * water at fire mobs to deal damage. The damage scales with the caster's skill level, ranging
 * from a minimum of 5 to a maximum of 20 health points.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target Detection:</strong> Searches for fire-based mobs as the projectile travels</li>
 * <li><strong>Damage Effect:</strong> Deals scaled damage to targeted fire mobs based on caster skill</li>
 * <li><strong>Water Block Effect:</strong> Places a temporary water block at the target's eye level</li>
 * <li><strong>Damage Range:</strong> Minimum {@link #minDamage} (5), maximum {@link #maxDamage} (20)</li>
 * </ul>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Aqua_Eructo">Aqua Eructo on Harry Potter Wiki</a>
 */
public final class AQUA_ERUCTO_DUO extends AQUA_ERUCTO {
    /**
     * Minimum damage dealt by this spell to fire-based mobs.
     */
    public static final int minDamage = 5;

    /**
     * Maximum damage dealt by this spell to fire-based mobs.
     */
    public static final int maxDamage = 20;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AQUA_ERUCTO_DUO(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AQUA_ERUCTO_DUO;
        branch = O2MagicBranch.CHARMS;

        text = "Shoots water from your wand that damages fire entities.";
    }

    /**
     * Constructor for casting the spell.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using (correctness factor)
     */
    public AQUA_ERUCTO_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.AQUA_ERUCTO_DUO;
        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Determines whether this spell can target a given entity.
     *
     * <p>An entity is a valid target if it is a fire-based mob (such as Blaze or Magma Cube).
     * This overrides the parent class targeting logic which checks for burning entities.</p>
     *
     * @param entity the entity to check
     * @return true if the entity is a fire mob, false otherwise
     */
    @Override
    boolean canTarget(Entity entity) {
        common.printDebugMessage("AQUA_ERUCTO_DUO.canTarget(): checking " + entity.getType(), null, null, false);

        return EntityCommon.isFireMob(entity.getType());
    }

    /**
     * Applies damage to a target fire mob based on caster skill.
     *
     * <p>Calculates damage as a fraction of the caster's uses modifier (skill level),
     * clamped between {@link #minDamage} and {@link #maxDamage}. Only living entities
     * can receive damage; non-living entities are skipped.</p>
     *
     * @param entity the fire mob to damage
     */
    @Override
    void effectEntity(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            common.printDebugMessage("AQUA_ERUCTO_DUO.effectEntity: not a living entity", null, null, true);
            return;
        }

        // cause damage based on caster's skill
        int damage = (int) (usesModifier / 10);

        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;

        ((LivingEntity) entity).damage(damage, player);
    }
}

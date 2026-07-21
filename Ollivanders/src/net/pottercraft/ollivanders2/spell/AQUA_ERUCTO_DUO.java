package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A variant of {@link AQUA_ERUCTO} that damages fire-based mobs (such as blazes and magma cubes) instead of
 * extinguishing burning targets. Damage scales with the caster's skill, limited to [{@link #minDamage},
 * {@link #maxDamage}].
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Aqua_Eructo">Harry Potter Wiki - Aqua Eructo</a>
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
     * @param entity the entity to check
     * @return true if the entity is a fire-based mob
     */
    @Override
    boolean canTarget(Entity entity) {
        common.printDebugMessage("AQUA_ERUCTO_DUO.canTarget(): checking " + entity.getType(), null, null, false);

        return EntityCommon.isFireMob(entity.getType());
    }

    /**
     * Damage a target fire mob by the caster's skill, limited to [{@link #minDamage}, {@link #maxDamage}]. Non-living
     * entities are skipped.
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

        ((LivingEntity) entity).damage(damage, caster);
    }
}

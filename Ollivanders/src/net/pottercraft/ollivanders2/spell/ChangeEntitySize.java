package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for spells that grow or shrink nearby living entities, toggling baby/adult for Ageable creatures and
 * scaling Slimes. The effect scales with the caster's skill.
 *
 * @author Azami7
 */
public abstract class ChangeEntitySize extends O2Spell {
    /**
     * Upper limit for {@link #targets}.
     */
    static int maxTargets = 10;

    /**
     * Upper limit for {@link #effectRadius}, in blocks.
     */
    static int maxEffectRadius = 20;

    /**
     * How many entities this cast will still affect. Set by {@link #calculateNumberOfTargets()}.
     */
    int targets;

    /**
     * The radius of entities affected around the projectile, in blocks. Set by {@link #calculateEffectRadius()}.
     */
    double effectRadius;

    /**
     * True if this spell grows its targets, false if it shrinks them.
     */
    boolean growing;

    /**
     * Upper limit for the per-cast change in a Slime's size.
     */
    static final int maxSlimeSizeChange = 2;

    /**
     * Smallest slime size this spell will produce.
     */
    static final int minSlimeSize = 1;

    /**
     * Largest slime size this spell will produce.
     */
    static final int maxSlimeSize = 3;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ChangeEntitySize(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ChangeEntitySize(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Grow or shrink up to {@link #targets} nearby living entities, ending the spell once that many have been affected
     * or the projectile hits a block.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock()) {
            kill();
            return;
        }

        for (LivingEntity livingEntity : getNearbyLivingEntities(effectRadius)) {
            if (targets < 1) {
                kill();
                return;
            }

            common.printDebugMessage("Checking " + livingEntity.getName(), null, null, false);

            if (livingEntity.getUniqueId().equals(caster.getUniqueId()))
                continue;

            if (!entityHarmWGCheck(livingEntity))
                continue;

            if (livingEntity instanceof Ageable) {
                changeEntityAge((Ageable) livingEntity);
            }
            else if (livingEntity instanceof Slime)
                changeSlimeSize((Slime) livingEntity);
            else
                continue;

            targets = targets - 1;
        }
    }

    /**
     * Set {@link #targets} to {@code usesModifier / 10}, limited to [1, {@link #maxTargets}].
     */
    void calculateNumberOfTargets() {
        targets = (int) (usesModifier / 10);

        if (targets < 1)
            targets = 1;
        else if (targets > maxTargets)
            targets = maxTargets;
    }

    /**
     * Set {@link #effectRadius} to {@code usesModifier / 10}, limited to [{@link #defaultRadius},
     * {@link #maxEffectRadius}].
     */
    void calculateEffectRadius() {
        effectRadius = usesModifier / 10;

        if (effectRadius < defaultRadius)
            effectRadius = defaultRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }

    /**
     * Toggle the entity between adult and baby per {@link #growing}. Hostile mobs are only changed when the caster's
     * skill is at least 100.
     *
     * @param entity the Ageable entity to change
     */
    private void changeEntityAge(@NotNull Ageable entity) {
        // only change hostile mob sizes when skill level is above 100
        if (EntityCommon.isHostile(entity) && usesModifier < 100)
            return;

        if (growing)
            entity.setAdult();
        else
            entity.setBaby();
    }

    /**
     * Grow or shrink the slime per {@link #growing} by a skill-scaled amount (capped at {@link #maxSlimeSizeChange}),
     * limiting the result to [{@link #minSlimeSize}, {@link #maxSlimeSize}].
     *
     * @param slime the Slime entity to resize
     */
    private void changeSlimeSize(@NotNull Slime slime) {
        int delta = (int) (usesModifier / 25) + 1;
        if (delta > maxSlimeSizeChange)
            delta = maxSlimeSizeChange;

        common.printDebugMessage("Changing slime size by " + delta, null, null, false);

        int newSize = slime.getSize();
        if (growing)
            newSize = newSize + delta;
        else
            newSize = newSize - delta;

        if (newSize > maxSlimeSize)
            newSize = maxSlimeSize;
        else if (newSize < minSlimeSize)
            newSize = minSlimeSize;

        slime.setSize(newSize);
    }

    /**
     * Returns whether this spell grows entities (true) or shrinks them (false).
     *
     * @return true if growing, false if shrinking
     */
    public boolean isGrowing() {
        return growing;
    }
}

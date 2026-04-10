package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for spells that change the size of living entities.
 *
 * <p>This class provides the core logic for spells that grow or shrink entities within a
 * target radius. Supports aging mechanics for Ageable creatures (baby/adult) and size changes
 * for Slimes based on player skill level.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Target Detection: Scans nearby radius for living entities (configurable per subclass)</li>
 * <li>Target Limit: Affects up to 10 entities per cast</li>
 * <li>Radius Limit: Maximum 20 blocks</li>
 * <li>Hostile Restrictions: Only affects hostile mobs if skill level exceeds 100</li>
 * <li>Size Changes: Slimes scale by 1-2 blocks based on skill level; Ageable creatures toggle baby/adult</li>
 * </ul>
 *
 * @author Azami7
 */
public abstract class ChangeEntitySize extends O2Spell {
    /**
     * Max number of entities that can be targeted
     */
    static int maxTargets = 10;

    /**
     * Max possible radius for the spell
     */
    static int maxEffectRadius = 20;

    /**
     * The number of entities to target
     */
    int targets;

    /**
     * The radius to affect
     */
    double effectRadius;

    /**
     * Is this spell growing or shrinking the entity?
     */
    boolean growing;

    /**
     * Maximum size change for Slimes per cast
     */
    static final int maxSlimeSizeChange = 2;

    /**
     * Minimum Slime size (smallest possible)
     */
    static final int minSlimeSize = 1;

    /**
     * Maximum Slime size (largest possible)
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ChangeEntitySize(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Scans for living entities within the effect radius and changes their size.
     *
     * <p>Each tick, this method searches for nearby living entities (up to the target limit)
     * and attempts to change their size if they are Ageable (baby/adult toggle) or Slimes
     * (size scaling). Respects WorldGuard entity harm restrictions and skips the caster.
     * Hostile entities can only be affected if the caster's skill level exceeds 100.</p>
     *
     * <p>Terminates after affecting the target count or if the spell projectile hits a block.</p>
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

    void calculateNumberOfTargets() {
        targets = (int) (usesModifier / 10);

        if (targets < 1)
            targets = 1;
        else if (targets > maxTargets)
            targets = maxTargets;
    }

    void calculateEffectRadius() {
        effectRadius = usesModifier / 10;

        if (effectRadius < defaultRadius)
            effectRadius = defaultRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }

    /**
     * Changes the age state (baby/adult) of an Ageable entity.
     *
     * <p>For hostile mobs, only applies the change if the caster's skill level exceeds 100.
     * Non-hostile creatures (farm animals, pets) can always be aged.</p>
     *
     * @param entity the Ageable entity to change (baby to adult, or adult to baby)
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
     * Changes the size of a Slime entity based on spell direction and skill level.
     *
     * <p>Size change is calculated as (usesModifier / 25) + 1, capped at maxSlimeSizeChange (2 blocks).
     * The new size is bounded between minSlimeSize (1) and maxSlimeSize (3) to keep Slimes valid.</p>
     *
     * @param slime the Slime entity to resize (grows or shrinks depending on spell type)
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

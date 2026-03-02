package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.player.Year;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Arresto Momentum - The Slowing Charm.
 *
 * <p>ARRESTO_MOMENTUM slows down the velocity of any item or living entity according to the caster's
 * spell experience level. The spell targets living entities first (if the caster's year level permits),
 * then falls back to items if no valid living entity is found. Living entities must be normal-sized
 * (bounding box height ≤ 2 blocks) to be targetable.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Prioritizes targeting living entities over items</li>
 * <li>Living entities must have bounding box height ≤ 2 blocks (excludes Giants, Ender Dragons, etc.)</li>
 * <li>Living entity targeting restricted to casters at Year 5 or higher (if years are enabled)</li>
 * <li>Falls back to nearest item if no valid living entity found</li>
 * <li>Does not target the caster</li>
 * <li>Velocity reduction scales with spell experience: 60% (novice) to 20% (expert)</li>
 * <li>Makes one final check at the projectile's stop location</li>
 * </ul>
 *
 * <p>Reference: <a href="https://harrypotter.fandom.com/wiki/Slowing_Charm">Harry Potter Wiki - Slowing Charm</a></p>
 */
public final class ARRESTO_MOMENTUM extends O2Spell {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ARRESTO_MOMENTUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ARRESTO_MOMENTUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("An incantation for slowing velocity.");
            add("\"Dumbledore ...ran onto the field as you fell, waved his wand, and you sort of slowed down before you hit the ground.\" - Hermione Granger");
            add("The witch Daisy Pennifold had the idea of bewitching the Quaffle so that if dropped, it would fall slowly earthwards as though sinking through water, meaning that Chasers could grab it in mid-air.");
        }};

        text = "Arresto Momentum will immediately slow down any entity or item.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ARRESTO_MOMENTUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ARRESTO_MOMENTUM;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * Check for nearby entities and items, then slow the first valid target found.
     *
     * <p>Searches for nearby living entities first (respecting year restrictions and size limits),
     * then falls back to items. When a valid target is found, applies velocity reduction based on
     * spell experience level, then kills the spell. If the projectile hits a solid block, the spell
     * is killed but still checks for targets at that location before dying.</p>
     */
    @Override
    protected void doCheckEffect() {
        // if the spell has hit a solid block, the projectile is dead and won't go further so kill the spell
        if (hasHitTarget())
            kill();

        Entity target = null;

        // check for living entities, unless years are enabled and the player is < 5th year
        if (!Ollivanders2.useYears || casterO2P.getYear().ordinal() >= Year.YEAR_5.ordinal()) {
            List<LivingEntity> entities = getNearbyLivingEntities(defaultRadius);

            for (LivingEntity entity : entities) {
                if (entity.getUniqueId().equals(caster.getUniqueId())) // do not target the caster
                    continue;

                if (entity.getBoundingBox().getHeight() > 2) // do not target entities bigger than 2 blocks
                    continue;

                target = entity;

                break;
            }
        }

        // check for items
        if (target == null) { // we did not find an entity to target
            List<Item> items = getNearbyItems(defaultRadius);

            if (!items.isEmpty())
                target = items.getFirst();
        }

        if (target != null) {
            changeVelocity(target);
            kill();
        }
    }

    /**
     * Reduce the target entity's velocity based on spell experience.
     *
     * <p>Calculates a velocity multiplier based on the caster's spell experience (usesModifier).
     * Higher experience results in stronger slowing (lower multiplier). The multiplier ranges from
     * 0.6 (novice, least slow) to 0.2 (expert, most slow). Applies the multiplier to the target's
     * current velocity vector.</p>
     *
     * @param entity the entity to slow down
     */
    void changeVelocity(Entity entity) {
        double velocityMultiplier;

        if (usesModifier > 100)
            velocityMultiplier = 0.2;
        else if (usesModifier > 75)
            velocityMultiplier = 0.3;
        else if (usesModifier > 50)
            velocityMultiplier = 0.4;
        else if (usesModifier > 25)
            velocityMultiplier = 0.5;
        else
            velocityMultiplier = 0.6;

        common.printDebugMessage("current speed = " + entity.getVelocity().length(), null, null, false);

        entity.setVelocity(entity.getVelocity().multiply(velocityMultiplier));

        common.printDebugMessage("new speed = " + entity.getVelocity().length(), null, null, false);
    }
}
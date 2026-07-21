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
 * The Slowing Charm: reduces the velocity of the first valid target by an amount that scales with caster skill. A
 * nearby living entity (never the caster, and only one no taller than 2 blocks) is preferred over the nearest item;
 * targeting a living entity also requires the caster to be at least Year 5 when years are enabled.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Slowing_Charm">Harry Potter Wiki - Slowing Charm</a>
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
     * Slow the first valid target near the projectile — a living entity if one qualifies, otherwise the nearest item —
     * then end the spell. A final check runs at the projectile's stop location before it dies.
     */
    @Override
    protected void doCheckEffect() {
        // if the spell has hit a solid block, the projectile is dead and won't go further so kill the spell
        if (hasHitBlock())
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
     * Multiply the target's velocity by a factor that shrinks with caster skill (0.6 for a novice down to 0.2 for an
     * expert), so higher skill slows it more.
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
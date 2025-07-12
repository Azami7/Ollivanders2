package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Arresto Momentum - https://harrypotter.fandom.com/wiki/Slowing_Charm - slows down any item or living entity according
 * to your level in the spell.
 */
public final class ARRESTO_MOMENTUM extends O2Spell {
    /**
     * The amount to affect the target's velocity
     */
    double multiplier = 0.5;

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
     * Set the velocity impact based on spell experience
     */
    @Override
    void doInitSpell() {
        if (usesModifier > 100)
            multiplier = 0;
        else if (multiplier > 75)
            multiplier = 0.2;
        else if (usesModifier > 50)
            multiplier = 0.3;
        else if (usesModifier > 25)
            multiplier = 0.4;
        else
            multiplier = 0.5;
    }

    /**
     * Checks for entities or items in a radius around the projectile and slows their velocity, if found
     */
    @Override
    protected void doCheckEffect() {
        // todo actually use the modifier
        double modifier = usesModifier;

        // check for entities first
        List<Entity> entities = getCloseEntities(defaultRadius);

        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                if (entity.getUniqueId() == player.getUniqueId())
                    continue;

                common.printDebugMessage("current speed = " + entity.getVelocity().length(), null, null, false);
                entity.setVelocity(entity.getVelocity().multiply(multiplier));

                common.printDebugMessage("new speed = " + entity.getVelocity().length(), null, null, false);

                kill();
                return;
            }

            return;
        }

        // if the spell has hit a solid block, the projectile is dead and won't go further so kill the spell
        if (hasHitTarget())
            kill();
    }
}
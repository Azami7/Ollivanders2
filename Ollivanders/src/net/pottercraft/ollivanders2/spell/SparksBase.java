package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base class for sparks spells like vermillious and verdimillious
 *
 * @since 2.21
 * @author Azami7
 */
public abstract class SparksBase extends O2Spell {
    /**
     * modifier on damage based on this spell's power, example if usesModifier is 200 (max) and damageModifier is 0.25,
     * damage = 0.25 (200/10) = 5, which would kill most small passive mobs like rabbits or fish
     */
    double damageModifier = 0;

    /**
     * damage this spell does, set based on experience
     */
    int damage = 0;

    /**
     * how much of a radius the strike can affect based this spell's power
     */
    int radius = 1;

    /**
     * Default constructor for use in generating spell book text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SparksBase(Ollivanders2 plugin)
    {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SparksBase(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        moveEffectData = Material.GLASS;
        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Set the damage done by this spell
     */
    void setDamage () {
        damage = (int)(damageModifier * (usesModifier / 10)) + 1;
    }

    @Override
    public void doCheckEffect() {
        // play the firework launch sound on the first tick, tick is incremented before doCheckEffect() is called so it starts at 1
        if (getLifeTicks() == 1) {
            World world = location.getWorld();
            if (world != null)
                world.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1,0);
        }

        if (hasHitTarget())
            kill();

        if (damage < 1)
            return;

        // check for entities this spell can damage
        List<LivingEntity> entities = getNearbyLivingEntities(radius);
        for (LivingEntity entity : entities)
        {
            // don't damage the caster
            if (entity.getUniqueId() == player.getUniqueId())
                continue;

            entity.damage(damage);

            kill();
            return;
        }
    }
}
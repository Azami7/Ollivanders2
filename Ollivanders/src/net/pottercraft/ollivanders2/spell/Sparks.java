package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base class for sparks spells that shoot a visual projectile and, optionally, damage the entity they hit. Damage and
 * radius scale with the caster's skill; every sparks spell plays a firework sound on impact.
 *
 * @author Azami7
 */
public abstract class Sparks extends O2Spell {
    /**
     * Scales damage with caster skill; see {@link #setDamage()} for the formula. 0 for non-damaging spells.
     */
    double damageModifier = 0;

    /**
     * The damage dealt on impact, set by {@link #setDamage()}. Only used when {@link #doDamage} is set.
     */
    double damage = 0;

    /**
     * If true, the spell damages the entity it hits; if false, it is purely visual.
     */
    boolean doDamage = false;

    /**
     * The radius in blocks searched for an entity to damage on impact.
     */
    double radius = defaultRadius;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Sparks(Ollivanders2 plugin) {
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
    public Sparks(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        moveEffectData = Material.GLASS;
        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Set {@link #damage} to {@code (damageModifier * (usesModifier / 10)) + 1}.
     */
    void setDamage() {
        damage = (damageModifier * (usesModifier / 10)) + 1;
    }

    /**
     * Play the firework sound on launch and, when {@link #doDamage} is set, damage the first nearby entity hit
     * (excluding the caster) before ending the spell. Killed when the projectile hits a block.
     */
    @Override
    public void doCheckEffect() {
        if (hasHitBlock())
            kill();

        // play the firework launch sound on the first tick, tick is incremented before doCheckEffect() is called so it starts at 1
        if (getProjectileAge() == 1) {
            world.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 0);
        }

        if (doDamage) { // look for an entity to damage if this spell does damage
            // check for entities this spell can damage
            List<LivingEntity> entities = getNearbyLivingEntities(radius);
            for (LivingEntity entity : entities) {
                // don't damage the caster
                if (entity.getUniqueId().equals(caster.getUniqueId()))
                    continue;

                setDamage();
                entity.damage(damage, caster);
                doOtherEffects(entity);

                kill();
                return;
            }
        }
    }

    /**
     * Hook for subclasses to apply extra effects to a damaged entity. The default implementation does nothing.
     *
     * @param entity the entity to affect
     */
    void doOtherEffects(@NotNull LivingEntity entity){}

    /**
     * @return true if this spell damages the entity it hits
     */
    public boolean doesDamage() {
        return doDamage;
    }
}
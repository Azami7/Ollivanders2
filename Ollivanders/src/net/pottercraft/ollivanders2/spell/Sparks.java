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
 * Base class for sparks spells that shoot projectiles to damage entities.
 *
 * <p>Sparks spells emit projectiles from the caster's wand that travel until hitting a target,
 * optionally dealing damage to nearby entities upon impact. Subclasses can customize:</p>
 * <ul>
 * <li>Whether the spell does damage via {@link #doDamage}</li>
 * <li>Damage amount via {@link #damageModifier} (scales with player skill)</li>
 * <li>Effect radius via {@link #radius}</li>
 * <li>Visual effect via {@link #moveEffectData}</li>
 * </ul>
 *
 * <p>All Sparks spells play a firework sound on impact and branch from CHARMS magic.</p>
 *
 * @author Azami7
 * @see VERDIMILLIOUS
 * @see VERDIMILLIOUS_DUO
 * @since 2.21
 */
public abstract class Sparks extends O2Spell {
    /**
     * Multiplier on damage calculation based on spell power.
     *
     * <p>Final damage is calculated as: {@code (damageModifier * (usesModifier / 10)) + 1}.
     * For example, with usesModifier=200 (max) and damageModifier=0.25, damage = 5,
     * which kills most small passive mobs like rabbits or fish.
     * Set to 0 for non-damaging spells.</p>
     */
    double damageModifier = 0;

    /**
     * The actual damage this spell deals to entities upon impact.
     *
     * <p>Calculated by {@link #setDamage()} when the spell hits a target.
     * Only used if {@link #doDamage} is true.</p>
     */
    double damage = 0;

    /**
     * Whether this spell damages entities it hits.
     *
     * <p>If false, the spell is purely visual/utility with no damage capability.
     * Subclasses should set this to true in their constructors if damage is desired.</p>
     */
    boolean doDamage = false;

    /**
     * The effect radius in blocks for detecting nearby entities to damage.
     *
     * <p>Determines how far from the impact location the spell searches for entities
     * to damage. Defaults to {@link #defaultRadius}. Subclasses can override this value.</p>
     */
    double radius = defaultRadius;

    /**
     * Default constructor for use in generating spell book text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Sparks(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Constructor for casting Sparks spells.
     *
     * <p>Initializes the spell with default configuration: GLASS visual effect and CHARMS branch.
     * Subclasses should set {@link #doDamage}, {@link #damageModifier}, and {@link #radius}
     * in their constructors before calling {@link #initSpell()}.</p>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand)
     */
    public Sparks(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        moveEffectData = Material.GLASS;
        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Calculates and sets the damage based on player skill and spell configuration.
     *
     * <p>Called when the spell hits a target entity. Computes damage using the formula:
     * {@code damage = (damageModifier * (usesModifier / 10)) + 1}.</p>
     */
    void setDamage() {
        damage = (damageModifier * (usesModifier / 10)) + 1;
    }

    /**
     * Executes spell effects each game tick.
     *
     * <p>Performs the following on each tick:</p>
     * <ul>
     * <li>Kills the spell if it has hit a target</li>
     * <li>Plays a firework sound on the first tick (projectileAge == 1)</li>
     * <li>If {@link #doDamage} is true, searches for nearby entities to damage and kills spell on hit</li>
     * </ul>
     */
    @Override
    public void doCheckEffect() {
        if (hasHitTarget())
            kill();

        // play the firework launch sound on the first tick, tick is incremented before doCheckEffect() is called so it starts at 1
        if (getProjectileAge() == 1) {
            World world = location.getWorld();
            if (world != null)
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
                entity.damage(damage);
                doOtherEffects(entity);

                kill();
                return;
            }
        }
    }

    /**
     * To be overridden by child classes that want to do other effects.
     *
     * @param entity the entity to affect
     */
    void doOtherEffects(LivingEntity entity){}

    /**
     * Checks if this spell damages entities.
     *
     * @return true if the spell deals damage, false otherwise
     */
    public boolean doesDamage() {
        return doDamage;
    }
}
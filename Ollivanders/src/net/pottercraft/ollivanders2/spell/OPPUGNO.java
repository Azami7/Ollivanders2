package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Causes a nearby living entity to attack the entity targeted by the caster.
 * <p>
 * On reaching a target, OPPUGNO finds another living entity within {@link #radius} blocks to act as
 * the attacker: the attacker is launched toward the target, set to actively target it if it is a
 * {@link Monster}, and the target takes {@link #damage} damage attributed to the attacker.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Oppugno_Jinx">Harry Potter Wiki - Oppugno Jinx</a>
 */
public final class OPPUGNO extends O2Spell {
    /**
     * The amount of damage to do
     */
    double damage;

    /**
     * The max damage this spell can do
     */
    static final double maxDamage = 6.0;

    /**
     * The min damage this spell will do
     */
    static final double minDamage = 0.5;

    /**
     * The radius this spell will look for attackers
     */
    private final int radius = 10;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public OPPUGNO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.OPPUGNO;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("Harry spun around to see Hermione pointing her wand at Ron, her expression wild: The little flock of birds was speeding like a hail of fat golden bullets toward Ron, who yelped and covered his face with his hands, but the birds attacked, pecking and clawing at every bit of flesh they could reach.");
            add("The Oppugno Jinx");
        }};

        text = "Oppugno will cause a creature or player to attack the targeted entity.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public OPPUGNO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.OPPUGNO;
        branch = O2MagicBranch.DARK_ARTS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.PVP);
            worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
        }

        initSpell();
    }

    /**
     * Set damage based on caster's spell experience
     */
    @Override
    void doInitSpell() {
        // make the damage based on caster skill
        damage = usesModifier / 20; // at spellMastery (100), damage would be 5
        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;
    }

    /**
     * Get the amount of damage this cast will do to the target.
     * <p>
     * Set by {@link #doInitSpell()} from the caster's skill and clamped to
     * {@link #getMinDamage()}..{@link #getMaxDamage()}, so the value is only meaningful after the
     * spell has been initialized for casting.
     * </p>
     *
     * @return the damage this cast will deal
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Get the minimum damage this spell will ever do, regardless of caster skill.
     *
     * @return the minimum damage
     */
    public double getMinDamage() {
        return minDamage;
    }

    /**
     * Get the maximum damage this spell can do, regardless of caster skill.
     *
     * @return the maximum damage
     */
    public double getMaxDamage() {
        return maxDamage;
    }

    /**
     * Find a target and an attacker near the projectile, then launch the attacker at the target and damage it.
     * <p>
     * The first living entity (other than the caster) within {@code defaultRadius} becomes the target,
     * which also stops the projectile. A second living entity within {@link #radius} that is neither the
     * caster nor the target becomes the attacker; if none exists, the spell is killed with no effect.
     * The attacker is set to target the victim (if it is a {@link Monster}), launched toward it, and the
     * target takes {@link #damage} damage credited to the attacker.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        // get the target to be attacked
        LivingEntity target = null;

        for (LivingEntity livingEntity : getNearbyLivingEntities(defaultRadius)) {
            if (livingEntity.getUniqueId().equals(caster.getUniqueId()))
                continue;

            target = livingEntity;
            stopProjectile();
            break;
        }

        if (target != null) {
            if (hasHitBlock())
                kill();

            // get an entity to attack the target
            LivingEntity attacker = null;

            for (LivingEntity livingEntity : getNearbyLivingEntities(radius)) {
                if (livingEntity.getUniqueId().equals(caster.getUniqueId()) || livingEntity.getUniqueId().equals(target.getUniqueId()))
                    continue;

                attacker = livingEntity;
                break;
            }

            if (attacker == null) {
                kill();
                return;
            }

            if (attacker instanceof Monster)
                ((Monster) attacker).setTarget(target);

            Vector targetPos = target.getLocation().toVector();
            Vector attackerPos = attacker.getLocation().toVector();
            Vector velocity = targetPos.subtract(attackerPos);

            // only launch the attacker if it is not already exactly on the target; normalizing a
            // zero-length vector yields NaN, which would produce an invalid velocity
            if (velocity.lengthSquared() > 0)
                attacker.setVelocity(velocity.normalize().multiply(2.0));

            target.damage(damage, attacker);
        }
    }
}
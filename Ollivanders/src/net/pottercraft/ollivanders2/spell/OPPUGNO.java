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
 * Causes a living entity to damage another living entity.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Oppugno_Jinx
 */
public final class OPPUGNO extends O2Spell
{
    /**
     * The amount of damage to do
     */
    double damage;

    /**
     * The max damage this spell can do
     */
    static double maxDamage = 6.0;

    /**
     * The min damage this spell will do
     */
    static double minDamage = 0.5;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public OPPUGNO(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.OPPUGNO;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>()
        {{
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
    public OPPUGNO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.OPPUGNO;
        branch = O2MagicBranch.DARK_ARTS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
        {
            worldGuardFlags.add(Flags.PVP);
            worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
        }

        initSpell();
    }

    /**
     * Set damage based on caster's spell experience
     */
    @Override
    void doInitSpell()
    {
        damage = usesModifier / 20;
        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;
    }

    @Override
    protected void doCheckEffect()
    {
        // get the target to be attacked
        LivingEntity target = null;

        for (LivingEntity livingEntity : getNearbyLivingEntities(defaultRadius))
        {
            if (livingEntity.getUniqueId() == player.getUniqueId())
                continue;

            target = livingEntity;
            stopProjectile();
        }

        if (target != null)
        {
            if (hasHitTarget())
                kill();

            // get an entity to attack the target
            LivingEntity attacker = null;

            for (LivingEntity livingEntity : getNearbyLivingEntities(10))
            {
                if (livingEntity.getUniqueId() == player.getUniqueId())
                    continue;

                attacker = livingEntity;
            }

            if (attacker == null)
            {
                kill();
                return;
            }

            if (attacker instanceof Monster)
                ((Monster) attacker).setTarget(target);

            Vector targetPos = target.getLocation().toVector();
            Vector attackerPos = attacker.getLocation().toVector();
            Vector velocity = targetPos.subtract(attackerPos);
            attacker.setVelocity(velocity.normalize().multiply(2.0));

            target.damage(damage, attacker);
        }
    }
}
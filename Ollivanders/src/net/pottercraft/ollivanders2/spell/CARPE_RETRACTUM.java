package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Seize and Pull Charm that pulls non-living entities toward the caster.
 *
 * <p>When the projectile hits a non-living entity, the spell pulls it toward the caster with velocity based on
 * the caster's skill level. This spell is guaranteed to work (minimum distance of 1) and has lower overall
 * distance and strength compared to similar spells.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Seize_and_pull_charm">Seize and Pull Charm</a>
 */
public final class CARPE_RETRACTUM extends Knockback {
    public static int minDistanceConfig = 1;
    public static int maxDistanceConfig = 5;
    public static int strengthReducerConfig = 4;
    public static double maxRadiusConfig = 10;
    public static int maxTargetConfig = 3;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CARPE_RETRACTUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CARPE_RETRACTUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"...which is why the Carpe Retractum spell is useful. It allows you to seize and pull objects within your direct line of sight towards you...\" -Professor Flitwick");
            add("Seize and Pull Charm");
        }};

        text = "Pulls an item towards you.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CARPE_RETRACTUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CARPE_RETRACTUM;
        branch = O2MagicBranch.CHARMS;

        // guananteed to work, lower overall distance, lower strengthReducer
        minDistance = minDistanceConfig;
        maxDistance = maxDistanceConfig;
        strengthReducer = strengthReducerConfig;
        maxRadius = maxRadiusConfig;
        maxTargets = maxTargetConfig;

        pull = true;

        initSpell();
    }

    /**
     * Can this spell target this entity?
     *
     * @param entity the entity to check
     * @return true if it can target the entity, false otherwise
     */
    boolean canTarget(Entity entity) {
        return !(entity instanceof LivingEntity);
    }
}
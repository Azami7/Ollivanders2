package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Winged-Ascent Charm that launches living entities high into the air.
 *
 * <p>When the projectile hits an entity, the spell adds an upward velocity based on the caster's skill level.
 * Horizontal velocity components (X and Z) remain unchanged.</p>
 *
 * <p>The spell targets entities within the projectile's detection radius and checks worldGuard permissions
 * before applying velocity. The projectile stops after hitting an entity or solid block.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Alarte_Ascendare">Alarte Ascendare</a>
 */
public final class ALARTE_ASCENDARE extends Knockback {
    public static int minDistanceConfig = 0;
    public static int maxDistanceConfig = 10;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ALARTE_ASCENDARE(Ollivanders2 plugin) {
        super(plugin);
        spellType = O2SpellType.ALARTE_ASCENDARE;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Winged-Ascent Charm");
            add("He brandished his wand at the snake and there was a loud bang; the snake, instead of vanishing, "
                    + "flew ten feet into the air and fell back to the floor with a loud smack.");
        }};

        text = "Shoots target living entity in to the air.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ALARTE_ASCENDARE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.ALARTE_ASCENDARE;
        branch = O2MagicBranch.CHARMS;

        minDistance = minDistanceConfig;
        maxDistance = maxDistanceConfig;
        isVertical = true;

        initSpell();
    }

    /**
     * Can this spell target this entity?
     *
     * @param entity the entity to check
     * @return true if it can target the entity, false otherwise
     */
    boolean canTarget(Entity entity) {
        return entity instanceof LivingEntity;
    }
}
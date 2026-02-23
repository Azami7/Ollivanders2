package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Climbing Charm that propels the caster upward.
 *
 * <p>When cast, the spell launches the caster into the air with an upward velocity based on their skill level.
 * If the caster is underwater, they are propelled to the water surface instead. The maximum distance is limited
 * to prevent fall damage upon landing.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Ascendio">Ascendio</a>
 */
public final class ASCENDIO extends Knockback {
    public static int minDistanceConfig = 0;
    public static int maxDistanceConfig = 3;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ASCENDIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ASCENDIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Climbing Charm");
            add("Underwater he casts a spell which propels him towards the surface, he flies out and lands on the decking where the crowd are.");
        }};

        text = "Propels the caster into the air or to the surface of the water, if underwater.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ASCENDIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.ASCENDIO;
        branch = O2MagicBranch.CHARMS;
        noProjectile = true; // spell targets self so should not create a spell projectile

        minDistance = 0;
        maxDistance = 3; // more than 3 and the caster will take fall damage on landing
        targetsSelf = true;
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
        return entity.getUniqueId().equals(player.getUniqueId());
    }
}
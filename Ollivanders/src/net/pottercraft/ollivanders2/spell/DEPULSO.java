package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Banishing Charm that pushes entities away from the caster.
 *
 * <p>When the projectile hits an entity, the spell pushes it away with force based on the caster's skill level.
 * This spell can target any entity type.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Banishing_Charm">Banishing Charm</a>
 */
public final class DEPULSO extends Knockback {
    public static int minDistanceConfig = 0;
    public static int maxDistanceConfig = 10;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DEPULSO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.DEPULSO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("They were supposed to be practising the opposite of the Summoning Charm today — the Banishing Charm. Owing to the potential for nasty accidents when objects kept flying across the room. Professor Flitwick had given each student a stack of cushions on which to practise, the theory being that these wouldn’t hurt anyone if they went off target.");
            add("The Banishing Charm");
        }};

        text = "Depulso will repel any entity you hit with it.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DEPULSO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DEPULSO;
        branch = O2MagicBranch.CHARMS;

        minDistance = minDistanceConfig;
        maxDistance = maxDistanceConfig;

        initSpell();
    }

    /**
     * Can this spell target this entity?
     *
     * @param entity the entity to check
     * @return true if it can target the entity, false otherwise
     */
    boolean canTarget(Entity entity) {
        return true; // depulso can target any entity type
    }
}
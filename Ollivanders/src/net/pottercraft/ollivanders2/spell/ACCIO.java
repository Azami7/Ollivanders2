package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Summoning Charm that pulls items toward the caster.
 *
 * <p>When the projectile hits an item, the spell pulls it toward the caster based on the caster's skill level.
 * The strength and distance of the pull are determined by the caster's experience with the spell.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Summoning_Charm">Summoning Charm</a>
 */
public final class ACCIO extends Knockback {
    public static int minDistanceConfig = 0;
    public static int maxDistanceConfig = 20;
    public static int strengthReducerConfig = 10;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ACCIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ACCIO;

        flavorText = new ArrayList<>() {{
            add("\"Accio Firebolt!\" -Harry Potter");
            add("The Summoning Charm");
        }};

        text = "Can be used to pull an item towards you. The strength of the pull is determined by your experience. "
                + "This can only be used on items.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ACCIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.ACCIO;
        branch = O2MagicBranch.CHARMS;

        pull = true;
        strengthReducer = strengthReducerConfig;
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
        return entity instanceof Item;
    }
}
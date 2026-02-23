package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Knockback Jinx that throws entities away from the caster.
 *
 * <p>When the projectile hits an entity, the spell pushes it away with force based on the caster's skill level.
 * This jinx is more powerful than Depulso and can target any entity type.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Knockback_Jinx">Knockback Jinx</a>
 */
public final class FLIPENDO extends Knockback {
    public static int minDistanceConfig = 2;
    public static int maxDistanceConfig = 10;
    public static int strengthReducerConfig = 10;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FLIPENDO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FLIPENDO;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("The Knockback Jinx");
            add("The incantation for the knockback jinx is 'Flipendo'. This jinx is the most utilitarian of Grade 2 spell, in that it will allow the caster to 'knock back' an opponent or object.");
            add("\"There was a loud bang and he felt himself flying backwards as if punched; as he slammed into the kitchen wall and slid to the floor, he glimpsed the tail of Lupin's cloak disappearing round the door.\"");
        }};

        text = "Flipendo can be used to repel an entity away from oneself.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FLIPENDO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FLIPENDO;
        branch = O2MagicBranch.DARK_ARTS;

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
        return true; // flipendo can target any entity type
    }
}
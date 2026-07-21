package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Bubble Jinx: traps a target player inside a hollow shell of white stained glass and immobilizes them.
 *
 * <p>Only the outer shell is built — the blocks the player occupies are left clear to avoid suffocation — and only
 * players at normal or reduced size (scale ≤ 1.0) can be trapped, since an oversized player would not fit inside it.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Ebublio_Jinx">Harry Potter Wiki - Ebublio Jinx</a>
 */
public class EBUBLIO extends ImmobilizePlayer {
    /**
     * Minimum ticks the target stays trapped, floored at 2 minutes.
     */
    private static final int minEffectDurationConfig = 2 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EBUBLIO(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.EBUBLIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Bubble Jinx");
        }};

        text = "Creates a prison of glass that surrounds the target player and immobilizes them.";
    }

    /**
     * Constructor for casting the spell.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using (correctness factor)
     */
    public EBUBLIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        moveEffectData = Material.BLUE_ICE;

        spellType = O2SpellType.EBUBLIO;
        branch = O2MagicBranch.CHARMS;

        fullImmobilize = false;
        minEffectDuration = minEffectDurationConfig;
        imprison = true;
        imprisonMaterial = Material.WHITE_STAINED_GLASS;
        prisonIsShell = true;

        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Check whether the target can be trapped by this spell. Oversized players do not fit inside the glass shell
     * and are rejected.
     *
     * @param target the player to validate as a potential target
     * @return true if the player's scale attribute is ≤ 1.0; false if it is larger or absent
     */
    boolean canTarget(Player target) {
        if (!Ollivanders2.testMode) {
            AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);

            if (scaleAttribute == null || scaleAttribute.getBaseValue() > 1.0) {
                common.printDebugMessage("Ebublio.canTarget: player scale > 1.0", null, null, false);
                return false;
            }
        }

        return true;
    }
}

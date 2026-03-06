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
 * The Bubble Jinx that traps a target player inside a shell of glass.
 *
 * <p>EBUBLIO entraps the target in a shell of white stained glass that cannot be broken by the
 * player inside. Only the outer shell is built — blocks the player occupies are left unchanged
 * to prevent suffocation damage. The spell only affects players at normal or reduced size (scale ≤ 1.0).</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Only targets players with scale attribute ≤ 1.0</li>
 * <li>Builds a shell of WHITE_STAINED_GLASS around the player's expanded bounding box</li>
 * <li>Blocks inside the player's own bounding box are not changed (shell only)</li>
 * <li>Uses partial immobilization (allows rotation but prevents movement)</li>
 * <li>Minimum effect duration: 2 minutes</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Ebublio_Jinx">Harry Potter Wiki - Ebublio Jinx</a>
 */
public class EBUBLIO extends ImmobilizePlayer {
    /**
     * Minimum effect duration set to 2 minutes to match CARCEREM_AQUATICUM behaviour.
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
     * Determine if a player can be targeted by this spell.
     *
     * <p>Only players with a scale attribute of 1.0 or lower can be targeted. Oversized players cannot
     * be trapped in the glass bubble due to the expanded bounding box being too small to contain them.</p>
     *
     * @param target the player to validate as a potential target
     * @return true if the player's scale is ≤ 1.0, false if oversized (or if scale attribute is null)
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

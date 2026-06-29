package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Repairs a damageable item the caster aims at, the standard Mending Charm.
 * <p>
 * Restores durability to ordinary tools and equipment, scaled by the caster's skill and clamped to the repair bounds
 * inherited from {@link ReparoBase}. For the stronger variant that mends diamond and netherite items, see
 * {@link DIAMAS_REPARO}.
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Mending_Charm">Harry Potter Wiki - Mending Charm</a>
 */
public final class REPARO extends ReparoBase {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public REPARO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.REPARO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Mending Charm");
            add("Mr. Weasley took Harry's glasses, gave them a tap of his wand and returned them, good as new.");
            add("The Mending Charm will repair broken objects with a flick of the wand.  Accidents do happen, so it is essential to know how to mend our errors.");
        }};

        text = "Repair the durability of a tool.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public REPARO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REPARO;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }
}
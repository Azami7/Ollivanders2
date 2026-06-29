package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A stronger version of {@link REPARO} for mending diamond and netherite items.
 * <p>
 * Overrides the inherited repair bounds and multiplier from {@link ReparoBase} so it can restore the much larger
 * durability of high-tier tools, repairing more per cast and up to a higher maximum than the base Mending Charm.
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Mending_Charm">Harry Potter Wiki - Mending Charm</a>
 */
public final class DIAMAS_REPARO extends ReparoBase {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DIAMAS_REPARO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.DIAMAS_REPARO;
        branch = O2MagicBranch.CHARMS;

        text = "A stronger version of Reparo, which repairs the durability of a tool.";
        flavorText.clear();
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DIAMAS_REPARO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.DIAMAS_REPARO;
        branch = O2MagicBranch.CHARMS;

        minRepair = 132; // the max durability for a stone sword
        maxRepair = 2032; // the max durability for a netherite sword
        repairMultiplier = 5.0f; // 200 uses would repair 1000 damage

        initSpell();
    }
}

package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A stronger version of Reparo for mending diamond and netherite items.
 * <p>
 * https://harrypotter.fandom.com/wiki/Mending_Charm
 */
public final class DIAMAS_REPARO extends REPARO
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DIAMAS_REPARO(Ollivanders2 plugin)
    {
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
    public DIAMAS_REPARO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REPARO;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
        {
            worldGuardFlags.add(Flags.ITEM_DROP);
            worldGuardFlags.add(Flags.ITEM_PICKUP);
        }

        minRepair = 132; // the max durability for a stone sword
        maxRepair = 2032; // the max durability for a netherite sword
        repairMultiplier = 5.0f; // 200 uses would repair 1000 damage

        initSpell();
    }
}

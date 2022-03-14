package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LAPIFORS extends ItemToEntityTransfiguration
{
    private static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 15;
    private static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LAPIFORS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.LAPIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>()
        {{
            add("\"Lapifors, the transformation of a small object into a rabbit\" -Hermione Granger");
        }};

        text = "The transfiguration spell Lapifors will transfigure an entity into a rabbit.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LAPIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.TRANSFIGURATION;
        spellType = O2SpellType.LAPIFORS;

        permanent = false;
        maxDuration = Ollivanders2Common.ticksPerMinute * 10; // 10 minutes
        targetType = EntityType.RABBIT;

        consumeOriginal = false;

        initSpell();
    }

    /**
     * Determine success rate and whether this spell is permanent based on player skill level
     */
    @Override
    void doInitSpell()
    {
        successRate = (int) (usesModifier / 2);

        if (usesModifier > 100)
        {
            consumeOriginal = true;
            permanent = true;
        }
    }
}

package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Vanishes items, boats, and minecarts.
 * <p>
 * https://harrypotter.fandom.com/wiki/Vanishing_Spell
 */
public final class EVANESCO extends EntityTransfiguration
{
    // todo rework to more align with https://harrypotter.fandom.com/wiki/Vanishing_Spell
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EVANESCO(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.EVANESCO;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>()
        {{
            add("The Vanishing Spell");
            add("The contents of Harry’s potion vanished; he was left standing foolishly beside an empty cauldron.");
        }};

        text = "Evanesco will vanish items, boats, and minecarts.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EVANESCO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.EVANESCO;
        branch = O2MagicBranch.TRANSFIGURATION;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
        {
            worldGuardFlags.add(Flags.USE);
            worldGuardFlags.add(Flags.BUILD);
        }

        entityAllowedList.add(EntityType.ITEM);
        entityAllowedList.addAll(EntityCommon.minecarts);
        entityAllowedList.addAll(EntityCommon.boats);

        durationModifier = 4.0;

        initSpell();
    }

    /**
     * Set the success rate for this spell based on the caster's skill and year (if enabled)
     */
    @Override
    void doInitSpell()
    {
        float successMultiplier = 1f;

        if (Ollivanders2.useYears)
        {
            O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
            if (o2p != null)
            {
                if (this.spellType.getLevel().ordinal() > o2p.getYear().getHighestLevelForYear().ordinal())
                    successMultiplier = 2f;
                else
                    successMultiplier = 0.25f;
            }
        }

        successRate = (int) ((usesModifier / 4) * successMultiplier);
    }

    /**
     * Remove the entity and set the transfigured entity to the original
     *
     * @param entity the entity to transfigure
     * @return the original entity, now removed/killed
     */
    @Override
    @Nullable
    protected Entity transfigureEntity(@NotNull Entity entity)
    {
        originalEntity.remove();

        return originalEntity;
    }
}
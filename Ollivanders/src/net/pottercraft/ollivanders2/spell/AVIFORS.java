package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.ParrotWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


/**
 * Transfigures entity into a parrot.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Avifors_Spell
 */
public final class AVIFORS extends FriendlyMobDisguise
{
    //todo make the entities this can target small
    private static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 15;
    private static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AVIFORS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.AVIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>()
        {{
            add("However, mastering a Transfiguration spell such as \"Avifors\" can be both rewarding and useful.");
        }};

        text = "Turns target entity in to a bird.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AVIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AVIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        targetType = EntityType.PARROT;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);
        ParrotWatcher watcher = (ParrotWatcher) disguise.getWatcher();
        watcher.setVariant(EntityCommon.getRandomParrotColor());
        watcher.setFlyingWithElytra(true);

        initSpell();

        // this needs to be done at the end because it needs to consider the usesModifier
        populateEntityAllowedList();
    }

    /**
     * Revert the entity back to their original form.
     */
    @Override
    public void revert()
    {
        disguise.getWatcher().setFlyingWithElytra(false);

        super.revert();
    }
}
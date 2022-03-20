package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Turn target player in to a dog, or in rare cases a wolf.
 */
public final class CANIS extends PlayerDisguise
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CANIS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.CANIS;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "Turns target player in to a dog.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CANIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.CANIS;
        branch = O2MagicBranch.TRANSFIGURATION;

        targetType = EntityType.WOLF;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);

        WolfWatcher watcher = (WolfWatcher) disguise.getWatcher();
        watcher.setAdult();

        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 10);
        if (rand < 9)
        {
            watcher.isTamed();
            watcher.setCollarColor(O2Color.getRandomPrimaryDyeableColor().getDyeColor());
        }

        initSpell();
    }

    @Override
    void doInitSpell()
    {
        calculateSuccessRate();
    }
}

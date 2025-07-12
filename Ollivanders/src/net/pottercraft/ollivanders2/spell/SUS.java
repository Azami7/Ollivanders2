package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PigWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Turn target player in to a pig.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Human_to_pig_spell">https://harrypotter.fandom.com/wiki/Human_to_pig_spell</a>
 */
public final class SUS extends PlayerDisguise
{
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SUS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.SUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "Turns target player in to a pig.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.SUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        targetType = EntityType.PIG;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);

        PigWatcher watcher = (PigWatcher) disguise.getWatcher();
        watcher.setAdult();

        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

        if (rand == 0)
            watcher.setSaddled(true);
        else
            watcher.setSaddled(false);

        initSpell();
    }

    /**
     * Calculate success rate based on player skill level
     */
    @Override
    void doInitSpell()
    {
        calculateSuccessRate();
    }
}
package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Turn a target player in to a cow.
 */
public final class BOS extends PlayerDisguise {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public BOS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.BOS;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "Turns target player in to a cow.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public BOS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.BOS;
        branch = O2MagicBranch.TRANSFIGURATION;

        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        if (rand == 0) // 1% chance
            targetType = EntityType.MOOSHROOM;
        else
            targetType = EntityType.COW;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);

        AgeableWatcher watcher = (AgeableWatcher) disguise.getWatcher();
        watcher.setAdult();

        initSpell();
    }

    /**
     * Calculate success rate based on player skill level
     */
    @Override
    void doInitSpell() {
        calculateSuccessRate();
    }
}

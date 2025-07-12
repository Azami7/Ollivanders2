package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Turn target player in to a horse.
 */
public final class EQUUS extends PlayerDisguise {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EQUUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.EQUUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "Turns target player in to a horse.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EQUUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.EQUUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        targetType = EntityType.HORSE;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);
        HorseWatcher watcher = (HorseWatcher) disguise.getWatcher();
        watcher.setAdult();

        watcher.setStyle(EntityCommon.getRandomHorseStyle());
        watcher.setColor(EntityCommon.getRandomHorseColor());

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

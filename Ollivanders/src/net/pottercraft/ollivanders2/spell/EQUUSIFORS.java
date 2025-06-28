package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Transfigures an entity into a horse.
 */
public final class EQUUSIFORS extends FriendlyMobDisguise {
    private static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 15;
    private static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EQUUSIFORS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.EQUUSIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "Turns target entity in to a horse.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EQUUSIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.EQUUSIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        targetType = EntityType.HORSE;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);
        HorseWatcher watcher = (HorseWatcher) disguise.getWatcher();
        watcher.setAdult();
        watcher.setStyle(EntityCommon.getRandomHorseStyle());
        watcher.setColor(EntityCommon.getRandomHorseColor());

        initSpell();

        // this needs to be done at the end because it needs to consider the usesModifier
        populateEntityAllowedList();
    }
}
package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.RabbitWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Transfigures an entity into a rabbit. This is a split of the Lapifors spell since we need to handle items and mobs
 * differently.
 *
 * <p>Reference: https://harrypotter.fandom.com/wiki/Lapifors_Spell</p>
 */
public final class LAGOMORPHA extends FriendlyMobDisguise {
    /**
     * Min duration for this transfiguration
     */
    private static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 15;

    /**
     * Max duration for this transfiguration
     */
    private static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LAGOMORPHA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LAGOMORPHA;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "The jinx Lagomorpha will transfigure a small creature into a rabbit.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LAGOMORPHA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.LAGOMORPHA;
        branch = O2MagicBranch.TRANSFIGURATION;

        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        targetType = EntityType.RABBIT;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);
        RabbitWatcher watcher = (RabbitWatcher) disguise.getWatcher();
        watcher.setAdult();
        watcher.setType(EntityCommon.getRandomRabbitType());

        initSpell();

        // this needs to be done at the end because it needs to consider the usesModifier
        populateEntityAllowedList();
    }

    /**
     * Add all small friendly mobs only to this spell.
     */
    @Override
    void populateEntityAllowedList() {
        // add all small mobs as allowed targets by default
        entityAllowedList.addAll(smallFriendlyMobs);
    }
}

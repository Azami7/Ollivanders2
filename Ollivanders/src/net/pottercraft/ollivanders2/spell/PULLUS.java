package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Turn target player in to a chicken.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Pullus">https://harrypotter.fandom.com/wiki/Pullus</a>
 */
public final class PULLUS extends PlayerDisguise {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PULLUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PULLUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "Turns target player in to a chicken.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PULLUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PULLUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        calculateSuccessRate();

        targetType = EntityType.CHICKEN;
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
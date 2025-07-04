package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.FallingBlockWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Turn animals to flower pots (approximation for water goblets).
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Vera_Verto">https://harrypotter.fandom.com/wiki/Vera_Verto</a>
 */
public final class VERA_VERTO extends FriendlyMobDisguise {
    private static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 15;
    private static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERA_VERTO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERA_VERTO;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("\"Could I have your attention please? Right, now, today, we will be transforming animals into water goblets. Like so. One, two, three. Vera Verto.\" -Minerva McGonagall");
        }};

        text = "Turns an animal in to a flower pot. Size of animal and duration of the spell depends on your experience.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VERA_VERTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.VERA_VERTO;
        branch = O2MagicBranch.TRANSFIGURATION;

        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        targetType = EntityType.FALLING_BLOCK;
        ItemStack flowerPot = new ItemStack(Material.FLOWER_POT, 1);
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MiscDisguise(disguiseType);
        FallingBlockWatcher watcher = (FallingBlockWatcher) disguise.getWatcher();
        watcher.setBlock(flowerPot);

        initSpell();

        // this needs to be done at the end because it needs to consider the usesModifier
        populateEntityAllowedList();
    }
}

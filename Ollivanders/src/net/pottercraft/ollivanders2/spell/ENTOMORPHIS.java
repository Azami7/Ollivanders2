package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Turn target player in to a spider.
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Insect_Hex">https://harrypotter.fandom.com/wiki/Insect_Hex</a>
 * @since 2.2.6
 */
public final class ENTOMORPHIS extends PlayerDisguise {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ENTOMORPHIS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ENTOMORPHIS;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("What wouldn't he give to strike now, to jinx Dudley so thoroughly he'd have to crawl home like an insect, struck dumb, sprouting feelers...");
            add("The Insect Jinx");
        }};

        text = "Entomorphis will transfigure a player into a spider for a duration dependent on your experience.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ENTOMORPHIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ENTOMORPHIS;
        branch = O2MagicBranch.DARK_ARTS;

        targetType = EntityType.SPIDER;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);

        initSpell();
    }

    @Override
    void doInitSpell() {
        calculateSuccessRate();
    }
}
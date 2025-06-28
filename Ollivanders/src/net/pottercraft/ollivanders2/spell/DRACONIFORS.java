package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Turn target entity in to a Dragon.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Draconifors_Spell">https://harrypotter.fandom.com/wiki/Draconifors_Spell</a>
 */
public final class DRACONIFORS extends FriendlyMobDisguise {
    // todo make this spawn phantoms and not dragons since the ender dragon is way too big
    private static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 15;
    private static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DRACONIFORS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.DRACONIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("The Draconifors Transfiguration");
            add("\"It was great! Now I can turn anything into dragons!\" -Hermione Granger");
        }};

        text = "The Draconifors spell turns an entity in to a dragon. It is one of the most challenging transfigurations owing to the size and power of the dragon form.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DRACONIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DRACONIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        targetType = EntityType.ENDER_DRAGON;
        disguiseType = DisguiseType.getType(targetType);
        disguise = new MobDisguise(disguiseType);

        initSpell();

        // this needs to be done at the end because it needs to consider the usesModifier
        populateEntityAllowedList();
    }

    /**
     * Set success rate based on caster's skill
     */
    @Override
    void doInitSpell() {
        if (usesModifier < 20)
            successRate = 5;
        else if (usesModifier < 100)
            successRate = 10;
        else
            successRate = 20;
    }
}
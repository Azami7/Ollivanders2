package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Counter-spell to the Levicorpus Jinx, removing its suspension effect from the target.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Liberacorpus">Harry Potter Wiki - Liberacorpus</a>
 */
public final class LIBERACORPUS extends RemoveO2Effect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LIBERACORPUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LIBERACORPUS;
        branch = O2MagicBranch.DEFENSE_AGAINST_THE_DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("The Levicorpus Counter-Spell");
            add("...he jerked his wand upwards; Snape fell into a crumpled heap on the ground.");
        }};

        text = "Liberacorpus counters the Levicorpus Jinx.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LIBERACORPUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LIBERACORPUS;
        branch = O2MagicBranch.DEFENSE_AGAINST_THE_DARK_ARTS;

        effectsAllowList.add(O2EffectType.SUSPENSION);

        initSpell();

        successModifier = 1.0f;
    }

    @Override
    void doInitSpell() {
        super.doInitSpell();
    }
}
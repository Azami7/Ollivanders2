package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Bombardment spell that creates an explosion and breaks nearby blocks.
 *
 * <p>A standard bombing spell with moderate blast radius and block-breaking power.
 * Can break blocks with blast resistance up to 1.5 and hardness up to 1.0.
 * Does not break doors.</p>
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Bombardment_Spell">Bombardment Spell</a>
 */
public final class BOMBARDA extends BombardaBase {
    static final double minEffectRadiusConfig = 2;
    static final double maxEffectRadiusConfig = 5;

    /**
     * Constructor for spell info generation. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public BOMBARDA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.BOMBARDA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"Bombarda?\"\n\"And wake up everyone in Hogwarts?\" -Albus Potter and Scorpius Malfoy");
            add("An explosion incantation.");
        }};

        text = "Bombarda creates a small but loud explosion which can break things.";
    }

    /**
     * Constructor to cast the Bombarda spell.
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting the spell
     * @param rightWand the wand being used
     */
    public BOMBARDA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.BOMBARDA;
        branch = O2MagicBranch.CHARMS;

        maxBlastResistance = 1.5;
        maxHardness = 1.0;
        breaksDoors = false;
        minEffectRadius = minEffectRadiusConfig;
        maxEffectRadius = maxEffectRadiusConfig;

        initSpell();
    }
}
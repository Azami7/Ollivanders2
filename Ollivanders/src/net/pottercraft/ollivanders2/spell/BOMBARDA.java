package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Bombardment Spell: a moderate explosion that breaks weaker blocks around the impact, but not doors.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Bombardment_Spell">Harry Potter Wiki - Bombardment Spell</a>
 */
public final class BOMBARDA extends BombardaBase {
    static final double minEffectRadiusConfig = 2;
    static final double maxEffectRadiusConfig = 5;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
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
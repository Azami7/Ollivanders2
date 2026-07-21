package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A stronger {@link BOMBARDA}: a larger explosion that breaks tougher blocks, including doors.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Bombarda_Maxima">Harry Potter Wiki - Bombarda Maxima</a>
 */
public final class BOMBARDA_MAXIMA extends BombardaBase {
    static final double minEffectRadiusConfig = 2;
    static final double maxEffectRadiusConfig = 6;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public BOMBARDA_MAXIMA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.BOMBARDA_MAXIMA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("A more powerful explosion incantation.");
            add("\"Come on, let’s get destroying... Confringo? Stupefy? Bombarda? Which would you use?\" -Albus Potter");
            add("\"I'll make short work of this. Bombarda Maxima.\" -Dolores Umbridge");
        }};

        text = "Bombarda Maxima creates an explosion twice as powerful as Bombarda.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public BOMBARDA_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.BOMBARDA_MAXIMA;
        branch = O2MagicBranch.CHARMS;

        maxBlastResistance = 3.0;
        maxHardness = 2.0;
        breaksDoors = true;
        minEffectRadius = minEffectRadiusConfig;
        maxEffectRadius = maxEffectRadiusConfig;

        initSpell();
    }
}
package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A more powerful variant of Bombarda that creates a larger explosion.
 *
 * <p>An advanced bombing spell with greater blast radius and block-breaking power.
 * Can break blocks with blast resistance up to 3.0 and hardness up to 2.0.
 * Capable of breaking doors.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Bombarda_Maxima">Bombarda Maxima</a>
 */
public final class BOMBARDA_MAXIMA extends BombardaBase {
    static final double minEffectRadiusConfig = 2;
    static final double maxEffectRadiusConfig = 6;

    /**
     * Constructor for spell info generation. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
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
     * Constructor to cast the Bombarda Maxima spell.
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting the spell
     * @param rightWand the wand being used
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
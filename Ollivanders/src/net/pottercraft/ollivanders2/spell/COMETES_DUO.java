package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Cometes Duo - Enhanced Orange Burst Fireworks with Effects.
 *
 * <p>Launches one or more orange burst-effect fireworks with trailing, flicker, and fade effects.
 * The fireworks fade to yellow and white, creating an enhanced visual display. The number of
 * fireworks spawned depends on the caster's experience level, up to a maximum of 10. Each
 * firework explodes in a burst pattern with orange color and includes trailing, flicker,
 * and yellow-to-white fade effects.</p>
 */
public final class COMETES_DUO extends Pyrotechnia {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public COMETES_DUO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.COMETES_DUO;
        branch = O2MagicBranch.CHARMS;

        text = "Creates one or more orange burst fireworks with trails, flicker, and fades to white and yellow.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public COMETES_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.COMETES_DUO;
        branch = O2MagicBranch.CHARMS;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.ORANGE);
        fireworkType = Type.BURST;

        trails = true;
        flicker = true;
        fade = true;

        fadeColors = new ArrayList<>();
        fadeColors.add(Color.YELLOW);
        fadeColors.add(Color.WHITE);

        maxFireworks = 10;

        initSpell();
    }
}

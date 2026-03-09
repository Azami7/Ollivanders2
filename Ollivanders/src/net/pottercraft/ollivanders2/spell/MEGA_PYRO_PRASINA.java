package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Mega Pyro Prasina - Enhanced Green and Lime Ball Fireworks.
 *
 * <p>Launches one or more large green and lime ball-burst fireworks with trailing effects.
 * The number of fireworks spawned depends on the caster's experience level, up to a maximum
 * of 10. Each firework explodes in a large ball pattern with dual green and lime colors
 * and includes trailing effects.</p>
 */
public class MEGA_PYRO_PRASINA extends Pyrotechnia {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MEGA_PYRO_PRASINA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.MEGA_PYRO_PRASINA;
        text = "Conjures large green ball fireworks with trails.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MEGA_PYRO_PRASINA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.MEGA_PYRO_PRASINA;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.GREEN);
        fireworkColors.add(Color.LIME);

        fireworkType = FireworkEffect.Type.BALL_LARGE;
        trails = true;
        maxFireworks = 10;

        initSpell();
    }
}

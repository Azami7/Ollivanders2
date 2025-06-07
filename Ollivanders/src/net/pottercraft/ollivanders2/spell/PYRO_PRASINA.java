package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Green fireworks
 */
public class PYRO_PRASINA extends Pyrotechnia {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PYRO_PRASINA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PYRO_PRASINA;
        text = "Conjures large green ball fireworks in the air.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PYRO_PRASINA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PYRO_PRASINA;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.GREEN);
        fireworkType = FireworkEffect.Type.BALL_LARGE;

        maxFireworks = 10;

        initSpell();
    }

    /**
     * Set the number of fireworks that can be cast based on the user's experience.
     */
    @Override
    void doInitSpell() {
        setNumberOfFireworks();
    }
}

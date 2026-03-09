package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Cometes - The Orange Burst Fireworks Spell.
 *
 * <p>Launches one or more orange burst-effect fireworks into the air. The number of fireworks
 * spawned depends on the caster's experience level, up to a maximum of 10. Each firework
 * explodes in a burst pattern with an orange color.</p>
 */
public final class COMETES extends Pyrotechnia {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public COMETES(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.COMETES;
        branch = O2MagicBranch.CHARMS;

        text = "Creates one or more orange burst fireworks.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public COMETES(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.COMETES;
        branch = O2MagicBranch.CHARMS;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.ORANGE);
        fireworkType = Type.BURST;

        maxFireworks = 10;

        initSpell();
    }

    /**
     * Calculate the number of fireworks to spawn on initialization.
     *
     * <p>Called during spell initialization to determine how many fireworks this spell
     * will launch based on the caster's experience level.</p>
     */
    @Override
    void doInitSpell() {
        setNumberOfFireworks();
    }
}

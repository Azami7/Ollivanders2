package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Porfyro Asteri - The Purple Star Fireworks Spell.
 *
 * <p>Launches one or more purple star-burst fireworks into the air. The number of fireworks
 * spawned depends on the caster's experience level, up to a maximum of 10. Each firework
 * explodes in a star pattern with a purple color.</p>
 */
public final class PORFYRO_ASTERI extends Pyrotechnia {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PORFYRO_ASTERI(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PORFYRO_ASTERI;
        branch = O2MagicBranch.CHARMS;

        text = "Conjures purple star fireworks in the sky.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PORFYRO_ASTERI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PORFYRO_ASTERI;
        branch = O2MagicBranch.CHARMS;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.PURPLE);
        fireworkType = Type.STAR;

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

package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Porfyro Asteri Tria - The Premium Purple and Fuchsia Star Fireworks with Trails and Fade Spell.
 *
 * <p>Launches one or more premium purple and fuchsia star-burst fireworks with trailing and fade
 * effects. The number of fireworks spawned depends on the caster's experience level, up to a
 * maximum of 15. Each firework explodes in a star pattern with dual purple and fuchsia colors,
 * includes trailing effects, shuffled explosion types, and fades to white.</p>
 */
public final class PORFYRO_ASTERI_TRIA extends Pyrotechnia {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PORFYRO_ASTERI_TRIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PORFYRO_ASTERI_TRIA;
        branch = O2MagicBranch.CHARMS;

        text = "Conjures purple star fireworks with trails and that fades to white.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PORFYRO_ASTERI_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PORFYRO_ASTERI_TRIA;
        branch = O2MagicBranch.CHARMS;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.PURPLE);
        fireworkColors.add(Color.FUCHSIA);

        trails = true;
        fade = true;
        fadeColors = new ArrayList<>();
        fadeColors.add(Color.WHITE);
        fireworkType = FireworkEffect.Type.STAR;
        shuffleTypes = true;
        maxFireworks = 15;

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

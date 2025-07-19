package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Creates harmless blue flames that are waterproof. These flames can be touched, penetrated, and held
 * without burning the holder.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Bluebell_Flames">https://harrypotter.fandom.com/wiki/Bluebell_Flames</a>
 * @since 2.21.4
 */
public final class LUMOS_FERVENS extends StationarySpell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LUMOS_FERVENS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LUMOS_FERVENS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("Bluebell Flames");
            add("\"She whipped out her wand, waved it, muttered something, and sent a jet of the same bluebell flames she had used on Snape at the plant. In a matter of seconds, the two boys felt it loosening its grip as it cringed away from the light and warmth.\"");
        }};

        text = "Creates harmless, waterproof blue flames that provide light and warmth.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LUMOS_FERVENS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LUMOS_FERVENS;
        branch = O2MagicBranch.CHARMS;

        // Set spell-specific parameters
        minRadius = net.pottercraft.ollivanders2.stationaryspell.LUMOS_FERVENS.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.LUMOS_FERVENS.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.LUMOS_FERVENS.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.LUMOS_FERVENS.maxDurationConfig;

        radiusModifier = 1;
        durationModifierInSeconds = 60; // 1 minute per experience level
        flairSize = 15;

        initSpell();
    }

    /**
     * Create the stationary spell.
     *
     * @return the stationary spell
     */
    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.LUMOS_FERVENS(p, player.getUniqueId(), location, radius, duration);
    }
}
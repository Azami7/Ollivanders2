package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Base class for the protective shield stationary spells.
 */
public abstract class ShieldSpell extends O2StationarySpell {
    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public ShieldSpell(@NotNull Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor for casting a new shield spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     */
    public ShieldSpell(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        super(plugin, pid, location);
    }
}

package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tranquillus: a stationary "zone of tranquility" that stops mobs inside its radius from acquiring targets and blocks
 * projectiles launched from within it, creating a safe area free of ranged attacks and mob aggression.
 *
 * @author Azami7
 */
public class TRANQUILLUS extends O2StationarySpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 20;

    /**
     * Minimum spell duration: 30 seconds.
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Maximum spell duration: 5 minutes.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public TRANQUILLUS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.TRANQUILLUS;
    }

    /**
     * Constructor for casting a new Tranquillus spell.
     *
     * @param plugin   the plugin instance
     * @param pid      the player ID casting the spell
     * @param location the center location of the spell
     * @param radius   the spell radius in blocks
     * @param duration the spell duration in ticks
     */
    public TRANQUILLUS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.TRANQUILLUS;

        setRadius(radius);
        setDuration(duration, false);
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    @Override
    public void upkeep() {
        age();
    }

    /**
     * Cancel target acquisition by any entity inside the spell radius.
     *
     * @param event the entity target event
     */
    @Override
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        if (isLocationInside(event.getEntity().getLocation()))
            event.setCancelled(true);
    }

    /**
     * Cancel any projectile launched from inside the spell radius.
     *
     * @param event the projectile launch event
     */
    @Override
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        if (isLocationInside(event.getLocation()))
            event.setCancelled(true);
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    @Override
    void doCleanUp() {
    }
}

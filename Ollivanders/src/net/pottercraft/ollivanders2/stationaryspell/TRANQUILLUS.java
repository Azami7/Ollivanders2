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
 * Tranquillus - The Zone of Tranquility Spell.
 *
 * <p>Creates a stationary zone of tranquility that prevents hostile mob targeting and projectile
 * launches within its radius. Any entities inside the spell area cannot acquire new targets or
 * launch projectiles, creating a safe zone free from ranged attacks and mob aggression.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Cancels EntityTargetEvent for entities inside the spell radius</li>
 * <li>Cancels ProjectileLaunchEvent for projectiles launched from inside the spell radius</li>
 * <li>Radius range: 5-20 blocks</li>
 * <li>Duration range: 30 seconds to 5 minutes</li>
 * <li>No special data to serialize/deserialize</li>
 * </ul>
 *
 * @see O2StationarySpell for the base stationary spell class
 */
public class TRANQUILLUS extends O2StationarySpell {
    /**
     * Minimum spell radius (5 blocks).
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius (20 blocks).
     */
    public static final int maxRadiusConfig = 20;

    /**
     * Minimum spell duration (30 seconds).
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Maximum spell duration (5 minutes).
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public TRANQUILLUS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.TRANQUILLUS;
    }

    /**
     * Constructor for casting the tranquillus spell.
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
        setDuration(duration);
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets the spell's radius boundaries (5-20 blocks) and duration boundaries (30 seconds to 30 minutes).</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Age the spell by one tick per call.
     *
     * <p>Called once per server tick to age the spell, reducing its remaining duration
     * until it expires and is removed.</p>
     */
    @Override
    public void upkeep() {
        age();
    }

    /**
     * Handle entity targeting events inside the spell area.
     *
     * <p>Cancels any EntityTargetEvent where the targeting entity is inside the spell radius,
     * preventing mobs from acquiring targets within the zone of tranquility.</p>
     *
     * @param event the entity target event to process
     */
    @Override
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        if (isLocationInside(event.getEntity().getLocation()))
            event.setCancelled(true);
    }

    /**
     * Handle projectile launch events inside the spell area.
     *
     * <p>Cancels any ProjectileLaunchEvent for projectiles launched from inside the spell radius,
     * preventing ranged attacks and projectile-based abilities from functioning within the zone.</p>
     *
     * @param event the projectile launch event to process
     */
    @Override
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        if (isLocationInside(event.getLocation()))
            event.setCancelled(true);
    }

    /**
     * Serializes the spell data for persistence.
     *
     * <p>The tranquillus spell has no extra data to serialize beyond the base spell properties,
     * so this method returns an empty map.</p>
     *
     * @return an empty map (the spell has no custom data to serialize)
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserializes spell data from saved state.
     *
     * <p>The tranquillus spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the spell ends.
     *
     * <p>The tranquillus spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }
}

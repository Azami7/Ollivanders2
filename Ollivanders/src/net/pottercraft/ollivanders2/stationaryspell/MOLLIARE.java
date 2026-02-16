package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A stationary protection spell that negates fall damage within a protected area.
 *
 * <p>The Molliare charm (Cushioning Charm) creates a protective barrier that prevents fall damage
 * to all entities within the spell's radius. Players and creatures falling within the protected
 * area will not take damage from the impact, no matter the height of the fall. The spell lasts
 * for a configurable duration based on caster skill level.</p>
 *
 * <p>Spell characteristics:
 * <ul>
 *   <li>Radius: 5-20 blocks (configurable)</li>
 *   <li>Duration: 30 seconds to 30 minutes (configurable)</li>
 *   <li>Effect: Negates fall damage for all entities within the protected area</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Cushioning_Charm">https://harrypotter.fandom.com/wiki/Cushioning_Charm</a>
 */
public class MOLLIARE extends O2StationarySpell {
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
     * Maximum spell duration (30 minutes).
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public MOLLIARE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.MOLLIARE;
    }

    /**
     * Constructs a new MOLLIARE spell cast by a player.
     *
     * <p>Creates a molliare charm at the specified location with the given radius and duration.
     * The spell will negate fall damage for all entities within the protected area.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public MOLLIARE(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.MOLLIARE;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
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
     * Age the spell by a tick
     */
    @Override
    public void upkeep() {
        age();
    }

    /**
     * Handles entity damage events and negates fall damage within the protected area.
     *
     * <p>When an entity takes fall damage within the spell's radius, this method cancels the
     * damage event, preventing any harm from the fall. Damage from other sources is not affected
     * by this spell.</p>
     *
     * @param event the entity damage event (not null)
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        Entity entity = event.getEntity(); // entity and location will never be null

        if (isLocationInside(entity.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("MOLLIARE: canceled EntityDamageEvent", null, null, false);
        }
    }

    /**
     * Serializes the molliare spell data for persistence.
     *
     * <p>The molliare spell has no extra data to serialize beyond the base spell properties,
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
     * Deserializes molliare spell data from saved state.
     *
     * <p>The molliare spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the molliare spell ends.
     *
     * <p>The molliare spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }
}
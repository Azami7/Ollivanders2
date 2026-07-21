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
 * Molliare (Cushioning Charm): a stationary spell that negates fall damage for any entity within its radius.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Cushioning_Charm">Harry Potter Wiki - Cushioning Charm</a>
 */
public class MOLLIARE extends O2StationarySpell {
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
     * Maximum spell duration: 30 minutes.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public MOLLIARE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.MOLLIARE;
    }

    /**
     * Constructor for casting a new Molliare spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public MOLLIARE(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.MOLLIARE;

        setRadius(radius);
        setDuration(duration, false);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
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
     * Cancel fall damage for any entity inside the spell radius. Other damage causes are unaffected.
     *
     * @param event the entity damage event
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        Entity entity = event.getEntity();

        if (isLocationInside(entity.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("MOLLIARE: canceled EntityDamageEvent", null, null, false);
        }
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
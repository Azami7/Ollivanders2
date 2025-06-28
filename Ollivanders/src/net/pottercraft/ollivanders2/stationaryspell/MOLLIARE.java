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
 * Negates fall damage.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Cushioning_Charm">https://harrypotter.fandom.com/wiki/Cushioning_Charm</a>
 * {@link net.pottercraft.ollivanders2.spell.MOLLIARE}
 */
public class MOLLIARE extends O2StationarySpell {
    /**
     * the min radius for this spell
     */
    public static final int minRadiusConfig = 5;
    /**
     * the max radius for this spell
     */
    public static final int maxRadiusConfig = 20;
    /**
     * the min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;
    /**
     * the max duration for this spell
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
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
     */
    public MOLLIARE(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin);
        spellType = O2StationarySpellType.MOLLIARE;
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;

        setPlayerID(pid);
        setLocation(location);
        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Age the spell by a tick
     */
    @Override
    public void checkEffect() {
        age();
    }

    /**
     * Handle player interact event
     *
     * @param event the event
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

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }
}
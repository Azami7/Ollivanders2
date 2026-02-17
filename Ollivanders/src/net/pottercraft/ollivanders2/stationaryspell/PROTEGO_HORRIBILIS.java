package net.pottercraft.ollivanders2.stationaryspell;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;

import org.jetbrains.annotations.NotNull;

/**
 * A stationary shield spell that blocks Dark Arts spells from entering the protected area.
 *
 * <p>Protego Horribilis creates a protective barrier that prevents Dark Arts spells from crossing the shield boundary.
 * The spell is a powerful defensive charm that intercepts malicious magic before it can reach those inside the protected
 * area. However, the spell is not powerful enough to stop the Killing Curse (Avada Kedavra), which bypasses the shield
 * due to its overwhelming dark magic.</p>
 *
 * <p>Spell characteristics:
 * <ul>
 *   <li>Radius: 5-30 blocks (configurable)</li>
 *   <li>Duration: 30 seconds to 30 minutes (configurable)</li>
 *   <li>Effect: Blocks Dark Arts spells from entering the protected area</li>
 *   <li>Exception: Avada Kedavra (Killing Curse) bypasses the shield</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_horribilis">https://harrypotter.fandom.com/wiki/Protego_horribilis</a>
 */
public class PROTEGO_HORRIBILIS extends ShieldSpell {
    /**
     * Minimum spell radius (5 blocks).
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius (30 blocks).
     */
    public static final int maxRadiusConfig = 30;

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
    public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;
    }

    /**
     * Constructs a new PROTEGO_HORRIBILIS spell cast by a player.
     *
     * <p>Creates a protective shield at the specified location with the given radius and duration.
     * The shield will block Dark Arts spells from entering the protected area.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets the spell's radius boundaries (5-30 blocks) and duration boundaries (30 seconds to 30 minutes).</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Age the spell by a tick.
     */
    @Override
    public void upkeep() {
        age();
    }

    /**
     * Serializes the protego horribilis spell data for persistence.
     *
     * <p>The protego horribilis spell has no extra data to serialize beyond the base spell properties,
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
     * Deserializes protego horribilis spell data from saved state.
     *
     * <p>The protego horribilis spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Blocks Dark Arts spells from crossing the shield boundary.
     *
     * <p>When a spell projectile moves across the shield boundary, this method checks if it is:
     * <ul>
     *   <li>A Dark Arts spell - if yes, blocks it (cancels the event)</li>
     *   <li>Avada Kedavra (Killing Curse) - if yes, allows it through (too powerful to stop)</li>
     *   <li>Any other spell - if yes, allows it through</li>
     * </ul>
     * </p>
     *
     * @param event the spell projectile movement event (not null)
     */
    @Override
    void doOnSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        O2SpellType type = event.getSpell().spellType;
        if (type == O2SpellType.AVADA_KEDAVRA)
            return;

        O2MagicBranch branch = event.getSpell().getMagicBranch();
        if (branch != O2MagicBranch.DARK_ARTS)
            return;

        Location toLocation = event.getTo();
        Location fromLocation = event.getFrom();

        if (isLocationInside(toLocation) && !isLocationInside(fromLocation)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cleans up when the protego horribilis spell ends.
     *
     * <p>The protego horribilis spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }
}
package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A stationary shield spell that creates a protective barrier preventing entities from entering.
 *
 * <p>Protego Totalum creates an area where:</p>
 * <ul>
 *   <li>Players cannot move into the protected area from outside</li>
 *   <li>Hostile mobs that enter have their AI disabled so they cannot move or attack</li>
 *   <li>Creatures cannot spawn inside the protected area</li>
 *   <li>Entities already inside the area can move freely, including leaving</li>
 * </ul>
 *
 * <p>Note: There is no EntityMoveEvent in Spigot, so hostile mobs are handled by disabling
 * their AI rather than blocking their movement directly.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_totalum">https://harrypotter.fandom.com/wiki/Protego_totalum</a>
 * @since 2.21
 */
public class PROTEGO_TOTALUM extends ShieldSpell {
    /**
     * Minimum spell radius (5 blocks).
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius (40 blocks).
     */
    public static final int maxRadiusConfig = 40;

    /**
     * Minimum spell duration (5 minutes).
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration (30 minutes).
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Tracks hostile entities that have had their AI disabled by this spell.
     *
     * <p>Used to restore AI to affected entities when the spell ends.</p>
     */
    ArrayList<LivingEntity> entitiesAffected = new ArrayList<>();

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_TOTALUM;
    }

    /**
     * Constructs a new PROTEGO_TOTALUM spell cast by a player.
     *
     * <p>Creates a protective barrier at the specified location with the given radius and duration.
     * The barrier will block players from entering, disable AI on hostile mobs, and prevent
     * creature spawns inside the protected area.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_TOTALUM;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets the spell's radius boundaries (5-40 blocks) and duration boundaries (5 to 30 minutes).</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Gets the entities currently affected by this spell (AI disabled).
     *
     * <p>Returns a copy of the affected entities list to prevent external modifications.</p>
     *
     * @return a list of entities with disabled AI (not null, may be empty)
     */
    public List<Entity> getTrackedAffectedEntities() {
        return new ArrayList<>(entitiesAffected);
    }

    /**
     * Ages the spell and manages hostile mobs inside the protected area.
     *
     * <p>Checks for hostile entities inside the spell area and disables their AI so they cannot
     * move or attack. Affected entities are tracked so their AI can be restored when the spell ends.</p>
     */
    @Override
    public void upkeep() {
        age();

        // there is no EntityMoveEvent in spigot so we need to track hostile mobs manually
        // check for hostile entities in the spell area and remove their AI so they do not move or attack
        for (LivingEntity entity : getLivingEntitiesInsideSpellRadius()) {
            if (!EntityCommon.isHostile(entity) || entitiesAffected.contains(entity))
                continue;

            entity.setAI(false);
            entitiesAffected.add(entity);
        }
    }

    /**
     * Prevents players from entering the protected area.
     *
     * <p>When a player attempts to move from outside the spell area to inside it, the movement
     * is cancelled and the player receives a message explaining they cannot enter. Players already
     * inside the protected area can move freely, including leaving the area.</p>
     *
     * @param event the player move event (not null)
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLoc = event.getTo();
        Location fromLoc = event.getFrom(); // will never be null

        if (toLoc.getWorld() == null || fromLoc.getWorld() == null)
            return;

        // they are already inside the protected area
        if (Ollivanders2Common.isInside(fromLoc, location, radius))
            return;

        if (Ollivanders2Common.isInside(toLoc, location, radius)) {
            event.setCancelled(true);
            common.printDebugMessage("PROTEGO_TOTALUM: canceled PlayerMoveEvent", null, null, false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (event.isCancelled()) {
                        flair(10);
                        event.getPlayer().sendMessage(Ollivanders2.chatColor + "A magical force prevents you moving here.");
                    }
                }
            }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
        }
    }

    /**
     * Prevents creatures from spawning inside the protected area.
     *
     * <p>When a creature spawn event occurs inside the spell area, the spawn is cancelled
     * to maintain the sanctity of the protected space.</p>
     *
     * @param event the creature spawn event (not null)
     */
    @Override
    void doOnCreatureSpawnEvent(@NotNull CreatureSpawnEvent event) {
        Entity entity = event.getEntity(); // will never be null

        if (isLocationInside(entity.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("PROTEGO_TOTALUM: canceled CreatureSpawnEvent", null, null, false);
        }
    }

    /**
     * Serializes the protego totalum spell data for persistence.
     *
     * <p>The spell has no extra data to serialize beyond the base spell properties. Affected
     * entities are not saved because they will be re-detected and re-affected when the server
     * restarts and the spell resumes.</p>
     *
     * @return an empty map (the spell has no custom data to serialize)
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserializes protego totalum spell data from saved state.
     *
     * <p>The spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the protego totalum spell ends.
     *
     * <p>Restores AI to all affected entities that are still alive, allowing them to move
     * and attack normally again.</p>
     */
    @Override
    void doCleanUp() {
        // turn back on the AI for the entities we affected by this spell
        for (LivingEntity entity : entitiesAffected) {
            if (!entity.isDead())
                entity.setAI(true);
        }
    }
}
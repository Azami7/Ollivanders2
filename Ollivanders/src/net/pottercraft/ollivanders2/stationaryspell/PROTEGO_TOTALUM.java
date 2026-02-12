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
import java.util.Map;
import java.util.UUID;

/**
 * Doesn't let entities pass into the protected area.
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO_TOTALUM}
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_totalum">https://harrypotter.fandom.com/wiki/Protego_totalum</a>
 * @since 2.21
 */
public class PROTEGO_TOTALUM extends ShieldSpell {
    /**
     * min radius for this spell
     */
    public static final int minRadiusConfig = 5;
    /**
     * max radius for this spell
     */
    public static final int maxRadiusConfig = 40;
    /**
     * min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;
    /**
     * max duration for this spell
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Keep track of the living entities we have turned the AI off for
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
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
     */
    public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_TOTALUM;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Age the spell by 1 tick
     */
    @Override
    public void upkeep() {
        age();

        // check for hostile entities in the spell area and remove their AI do they do not move or attack
        for (LivingEntity entity : getLivingEntitiesInsideSpellRadius()) {
            if (EntityCommon.isHostile(entity) || entitiesAffected.contains(entity))
                continue;

            entity.setAI(false);
            entitiesAffected.add(entity);
        }
    }

    /**
     * Prevent players from entering the protected area
     *
     * @param event the player move event
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
     * Prevent entities from spawning in the protected area
     *
     * @param event the creature spawn event
     */
    @Override
    void doOnCreatureSpawnEvent(@NotNull CreatureSpawnEvent event) {
        Entity entity = event.getEntity(); // will never be null
        Location entityLocation = entity.getLocation();

        if (Ollivanders2Common.isInside(entityLocation, location, radius)) {
            event.setCancelled(true);
            common.printDebugMessage("PROTEGO_TOTALUM: canceled CreatureSpawnEvent", null, null, false);
        }
    }

    /**
     * Serialize all data specific to this spell so it can be saved. We do not need to save information about the entuties we
     * affected because we'll reset them and then reaffect them when the server restarts.
     *
     * @return a map of the serialized data
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserialize the data for this spell and load the data to this spell.
     *
     * @param spellData a map of the saved spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Turn the AI back on for all the entities we affected that are still alive.
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
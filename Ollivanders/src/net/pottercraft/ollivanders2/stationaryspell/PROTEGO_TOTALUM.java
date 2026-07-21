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
 * Protego Totalum: a stationary shield over an area that:
 * <ul>
 *   <li>Stops players from moving in from outside (those already inside may move and leave freely)</li>
 *   <li>Disables the AI of hostile mobs inside so they can neither move nor attack</li>
 *   <li>Prevents creatures from spawning inside</li>
 * </ul>
 * <p>
 * Spigot has no entity-move event, so hostile mobs are neutralized by disabling their AI rather than blocking movement.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_totalum">Harry Potter Wiki - Protego totalum</a>
 */
public class PROTEGO_TOTALUM extends ShieldSpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 40;

    /**
     * Minimum spell duration: 5 minutes.
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration: 30 minutes.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Hostile entities whose AI this spell disabled, tracked so {@link #doCleanUp()} can restore it when the spell ends.
     */
    ArrayList<LivingEntity> entitiesAffected = new ArrayList<>();

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_TOTALUM;
    }

    /**
     * Constructor for casting a new Protego Totalum spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_TOTALUM;

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

    /**
     * Get the entities whose AI this spell has disabled.
     *
     * @return a copy of the affected entities; empty if none
     */
    public List<Entity> getTrackedAffectedEntities() {
        return new ArrayList<>(entitiesAffected);
    }

    /**
     * Age the spell and disable the AI of any newly detected hostile mob inside the area, tracking it for later
     * restoration.
     */
    @Override
    public void upkeep() {
        age();

        // no EntityMoveEvent in Spigot, so hostile mobs inside are neutralized by disabling their AI here
        for (LivingEntity entity : getLivingEntitiesInsideSpellRadius()) {
            if (!EntityCommon.isHostile(entity) || entitiesAffected.contains(entity))
                continue;

            entity.setAI(false);
            entitiesAffected.add(entity);
        }
    }

    /**
     * Block a player crossing into the area from outside, with a flair and message. Players already inside move freely.
     *
     * @param event the player move event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLoc = event.getTo();
        Location fromLoc = event.getFrom();

        if (toLoc.getWorld() == null || fromLoc.getWorld() == null)
            return;

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
     * Cancel any creature spawn inside the area.
     *
     * @param event the creature spawn event
     */
    @Override
    void doOnCreatureSpawnEvent(@NotNull CreatureSpawnEvent event) {
        Entity entity = event.getEntity();

        if (isLocationInside(entity.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("PROTEGO_TOTALUM: canceled CreatureSpawnEvent", null, null, false);
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

    /**
     * Restore AI to every still-living entity this spell disabled.
     */
    @Override
    void doCleanUp() {
        for (LivingEntity entity : entitiesAffected) {
            if (!entity.isDead())
                entity.setAI(true);
        }
    }
}
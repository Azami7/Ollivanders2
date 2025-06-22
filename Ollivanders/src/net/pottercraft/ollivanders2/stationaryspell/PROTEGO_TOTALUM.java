package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Doesn't let entities pass into the protected area.
 * <p>
 * https://harrypotter.fandom.com/wiki/Protego_totalum
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO_TOTALUM}
 */
public class PROTEGO_TOTALUM extends ShieldSpell
{
    /**
     * min radius for this spell
     */
    public static final int minRadiusConfig = 5;
    /**
     * max radius for this spell
     */
    public static final int maxRadiusConfig = 30;
    /**
     * min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;
    /**
     * max duration for this spell
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin)
    {
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
    public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration)
    {
        super(plugin);
        spellType = O2StationarySpellType.PROTEGO_TOTALUM;

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
     * Age the spell by 1 tick
     */
    @Override
    void checkEffect()
    {
        age();
    }

    /**
     * Prevent players from entering the protected area
     *
     * @param event the player move event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event)
    {
        Location toLoc = event.getTo();
        Location fromLoc = event.getFrom(); // will never be null

        if (toLoc == null || toLoc.getWorld() == null || fromLoc.getWorld() == null)
            return;

        // they are already inside the protected area
        if (Ollivanders2Common.isInside(fromLoc, location, radius))
            return;

        if (Ollivanders2Common.isInside(toLoc, location, radius))
        {
            event.setCancelled(true);
            common.printDebugMessage("PROTEGO_TOTALUM: canceled PlayerMoveEvent", null, null, false);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (event.isCancelled())
                    {
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
    void doOnCreatureSpawnEvent(@NotNull CreatureSpawnEvent event)
    {
        Entity entity = event.getEntity(); // will never be null
        Location entityLocation = entity.getLocation();

        if (Ollivanders2Common.isInside(entityLocation, location, radius))
        {
            event.setCancelled(true);
            common.printDebugMessage("PROTEGO_TOTALUM: canceled CreatureSpawnEvent", null, null, false);
        }
    }

    /**
     * Prevent entities inside the protected area from being targeted
     *
     * @param event the event
     */
    @Override
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event)
    {
        Entity target = event.getTarget();
        if (target == null)
            return;

        Location targetLocation = target.getLocation();

        if (isLocationInside(targetLocation))
        {
            event.setCancelled(true);
            common.printDebugMessage("PROTEGO_TOTALUM: canceled EntityTargetEvent", null, null, false);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (event.isCancelled() && target instanceof Player)
                    {
                        flair(10);
                        target.sendMessage(Ollivanders2.chatColor + "A magical force protects you.");
                    }
                }
            }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
        }
    }

    /**
     * Serialize all data specific to this spell so it can be saved.
     *
     * @return a map of the serialized data
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData()
    {
        return new HashMap<>();
    }

    /**
     * Deserialize the data for this spell and load the data to this spell.
     *
     * @param spellData a map of the saved spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData)
    {
    }
}
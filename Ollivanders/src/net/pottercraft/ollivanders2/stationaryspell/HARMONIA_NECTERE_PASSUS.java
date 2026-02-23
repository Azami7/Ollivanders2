package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A stationary spell that creates a paired teleportation portal (vanishing cabinet).
 *
 * <p>Harmonia Nectere Passus creates a vanishing cabinet that teleports players to its twin
 * cabinet when they enter. The spell:
 * <ul>
 *   <li>Maintains a paired connection with another vanishing cabinet</li>
 *   <li>Teleports players between the two cabinets when they move into one</li>
 *   <li>Includes a cooldown to prevent immediate re-teleportation</li>
 *   <li>Verifies cabinet structural integrity on each upkeep</li>
 *   <li>Disables itself if the twin cabinet is destroyed or the structure is compromised</li>
 * </ul>
 * </p>
 *
 * <p>The spell is permanent and persists across server restarts.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Vanishing_Cabinet">https://harrypotter.fandom.com/wiki/Vanishing_Cabinet</a>
 */
public class HARMONIA_NECTERE_PASSUS extends O2StationarySpell {
    /**
     * Minimum spell radius (1 block) - cabinet must be exactly at the spell location.
     */
    public static final int minRadiusConfig = 1;

    /**
     * Maximum spell radius (1 block) - cabinet must be exactly at the spell location.
     */
    public static final int maxRadiusConfig = 1;

    /**
     * Minimum spell duration (1000 ticks) - not used, harmonia nectere passus is permanent.
     */
    public static final int minDurationConfig = 1000;

    /**
     * Maximum spell duration (1000 ticks) - not used, harmonia nectere passus is permanent.
     */
    public static final int maxDurationConfig = 1000;

    /**
     * The location of this cabinet's twin
     */
    private Location twinCabinetLocation;

    /**
     * The label for this spell's twin for serializing
     */
    private final String twinLabel = "Twin";

    /**
     * Players currently using this pair of vanishing cabinets
     */
    HashMap<Player, Integer> inUseBy = new HashMap<>();

    /**
     * The cooldown between uses
     */
    public final static int cooldown = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public HARMONIA_NECTERE_PASSUS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.HARMONIA_NECTERE_PASSUS;
        this.radius = minRadius;
        permanent = true;
    }

    /**
     * Constructs a new HARMONIA_NECTERE_PASSUS spell (vanishing cabinet).
     *
     * <p>Creates a paired vanishing cabinet at the specified location that connects to another
     * cabinet at the twin location. When players enter this cabinet, they are teleported to
     * the twin cabinet with a cooldown to prevent infinite loops.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of this cabinet (not null)
     * @param twin     the location of the paired cabinet (not null)
     */
    public HARMONIA_NECTERE_PASSUS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull Location twin) {
        super(plugin, pid, location);

        spellType = O2StationarySpellType.HARMONIA_NECTERE_PASSUS;
        permanent = true;

        radius = minRadius;
        duration = 10;

        this.twinCabinetLocation = twin;
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets fixed radius and duration values (not configurable). The spell is always permanent.</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig; // not used, harmonia is permanent
        maxDuration = maxDurationConfig; // not used, harmonia is permanent
    }

    /**
     * Ages the spell and validates cabinet integrity and twin connection.
     *
     * <p>Each tick, verifies:
     * <ul>
     *   <li>The cabinet structure is intact and valid</li>
     *   <li>The twin cabinet still exists</li>
     *   <li>Cooldowns for players using the cabinet are properly decremented</li>
     * </ul>
     * If any checks fail, both cabinets are destroyed.</p>
     */
    @Override
    public void upkeep() {
        World world = location.getWorld();
        if (world == null) {
            common.printDebugMessage("HARMONIA_NECTERE_PASSUS.checkEffect: world is null", null, null, false);
            kill();
            return;
        }

        HARMONIA_NECTERE_PASSUS twinCabinet = getTwin();

        if (twinCabinet == null) {
            common.printDebugMessage("Harmonia stationary: twin cabinet null", null, null, true);
            kill();
            return;
        }

        if (!cabinetCheck(location.getBlock())) {
            common.printDebugMessage("Harmonia stationary: cabinet malformed", null, null, true);
            kill();
            twinCabinet.kill();
            return;
        }

        // upkeep on inUseBy
        HashMap<Player, Integer> temp = new HashMap<>(inUseBy);

        for (Map.Entry<Player, Integer> entry : temp.entrySet()) {
            Player player = entry.getKey();
            Integer cooldown = entry.getValue();

            if (Ollivanders2Common.locationEquals(player.getLocation(), location))
                continue;

            if (cooldown < 0)
                inUseBy.remove(player);
            else
                inUseBy.put(player, cooldown - 1);
        }
    }

    /**
     * Get the twin for this cabinet.
     *
     * @return the twin if found, null otherwise
     */
    @Nullable
    public HARMONIA_NECTERE_PASSUS getTwin() {
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
            if (stationarySpell instanceof HARMONIA_NECTERE_PASSUS && Ollivanders2Common.locationEquals(stationarySpell.location, twinCabinetLocation))
                return (HARMONIA_NECTERE_PASSUS) stationarySpell;
        }

        return null;
    }

    /**
     * Is this cabinet in use by the player
     *
     * @param player the player to check
     * @return true if this player is using this cabinet, false otherwise
     */
    public boolean isUsing(Player player) {
        return inUseBy.containsKey(player);
    }

    /**
     * Checks the structural integrity of a vanishing cabinet.
     *
     * <p>Verifies the cabinet has the correct structure: a 3x3 space with solid walls on all sides,
     * space for a player, and a solid top. The feet position must be air or a sign (for labeling).</p>
     *
     * @param feet the block at the player's feet position in the cabinet
     * @return true if the cabinet structure is valid, false if not
     */
    private boolean cabinetCheck(@NotNull Block feet) {
        // feet block can either be air or a sign block
        if (feet.getType() != Material.AIR && !Ollivanders2Common.isSign(feet))
            return false;

        return (feet.getRelative(1, 0, 0).getType() != Material.AIR && feet.getRelative(1, 1, 0).getType() != Material.AIR
                && feet.getRelative(-1, 0, 0).getType() != Material.AIR && feet.getRelative(-1, 1, 0).getType() != Material.AIR
                && feet.getRelative(0, 0, 1).getType() != Material.AIR && feet.getRelative(0, 1, 1).getType() != Material.AIR
                && feet.getRelative(0, 0, -1).getType() != Material.AIR && feet.getRelative(0, 1, -1).getType() != Material.AIR
                && feet.getRelative(0, 1, 0).getType() == Material.AIR && feet.getRelative(0, 2, 0).getType() != Material.AIR);
    }

    /**
     * Serializes the vanishing cabinet's twin location for persistence.
     *
     * <p>Saves the location of the paired cabinet so it can be restored on server restart.</p>
     *
     * @return a map containing the serialized twin cabinet location
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        Map<String, String> serializedLoc = common.serializeLocation(twinCabinetLocation, twinLabel);

        if (serializedLoc == null)
            serializedLoc = new HashMap<>();

        return serializedLoc;
    }

    /**
     * Deserializes the vanishing cabinet's twin location from saved state.
     *
     * <p>Restores the location of the paired cabinet when the spell is loaded on server restart.</p>
     *
     * @param spellData a map containing the serialized cabinet data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
        Location loc = common.deserializeLocation(spellData, twinLabel);

        if (loc != null)
            twinCabinetLocation = loc;
    }

    /**
     * Teleports players to the twin cabinet when they enter this one.
     *
     * <p>When a player moves into this cabinet's location, they are teleported to the twin cabinet.
     * A cooldown is applied to both cabinets to prevent immediate re-teleportation.</p>
     *
     * @param event the player move event (not null)
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer(); // will never be null
        // make sure player is not already using this vanishing cabinet
        if (isUsing(player))
            return;

        Location toLoc = event.getTo();
        if (toLoc == null || !isLocationInside(toLoc))
            return;

        // make sure they actually moved locations, not turned head, etc
        if (Ollivanders2Common.locationEquals(toLoc, event.getFrom()))
            return;

        HARMONIA_NECTERE_PASSUS twin = getTwin();
        if (twin == null) {
            // we cannot teleport them because the other cabinet spell is gone
            kill();
            return;
        }
        else if (twin.isUsing(player)) {
            // make sure they do not have a use cooldown on the twin, or we'll just teleport them right back as soon as they arrive and trigger a move event
            return;
        }

        inUseBy.put(player, HARMONIA_NECTERE_PASSUS.cooldown);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.isCancelled()) {
                    p.addTeleportAction(player, twinCabinetLocation, true);
                }
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Cleans up when the vanishing cabinet spell ends.
     *
     * <p>The spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }

    /**
     * Validates that the spell was properly deserialized with all required data.
     *
     * <p>Checks that the caster UUID, cabinet location, and twin cabinet location are all present.</p>
     *
     * @return true if all required data is present, false otherwise
     */
    @Override
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null && twinCabinetLocation != null;
    }
}

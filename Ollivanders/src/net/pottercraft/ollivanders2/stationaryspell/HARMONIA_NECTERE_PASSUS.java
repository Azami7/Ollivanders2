package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
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
 * Harmonia Nectere Passus: a permanent stationary spell placed on a vanishing cabinet that teleports a player who steps
 * into it to its twin cabinet. A per-player cooldown prevents immediate re-teleport. Each upkeep re-checks that the
 * cabinet structure and its twin still exist, and kills both cabinets if not.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Vanishing_Cabinet">Harry Potter Wiki - Vanishing Cabinet</a>
 */
public class HARMONIA_NECTERE_PASSUS extends O2StationarySpell {
    /**
     * Minimum spell radius, in blocks; the cabinet occupies exactly the spell location.
     */
    public static final int minRadiusConfig = 1;

    /**
     * Maximum spell radius, in blocks; the cabinet occupies exactly the spell location.
     */
    public static final int maxRadiusConfig = 1;

    /**
     * Duration bound, unused because this spell is permanent.
     */
    public static final int minDurationConfig = 1000;

    /**
     * Duration bound, unused because this spell is permanent.
     */
    public static final int maxDurationConfig = 1000;

    /**
     * The location of this cabinet's twin, the destination players are sent to.
     */
    private Location twinCabinetLocation;

    /**
     * Serialization key for {@link #twinCabinetLocation}.
     */
    private final String twinLabel = "Twin";

    /**
     * Players currently on teleport cooldown, mapped to their remaining cooldown ticks.
     */
    HashMap<Player, Integer> inUseBy = new HashMap<>();

    /**
     * Teleport cooldown per player, in ticks.
     */
    public final static int cooldown = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
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
     * Constructor for casting a new Harmonia Nectere Passus spell, pairing this cabinet with the one at {@code twin}.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of this cabinet
     * @param twin     the location of the paired cabinet
     */
    public HARMONIA_NECTERE_PASSUS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull Location twin) {
        super(plugin, pid, location);

        spellType = O2StationarySpellType.HARMONIA_NECTERE_PASSUS;
        permanent = true;

        radius = minRadius;
        duration = 10;

        this.twinCabinetLocation = twin;
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Verify the twin cabinet still exists and this cabinet's structure is intact, killing both cabinets if not, then
     * count down the per-player teleport cooldowns.
     */
    @Override
    public void upkeep() {
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
     * Check that a vanishing cabinet is well-formed: solid blocks on all four sides at foot and head height, a solid
     * top, air where the player stands, and air or a sign at the foot block.
     *
     * @param signBlock the block at the player's foot position in the cabinet
     * @return true if the cabinet structure is valid, false if not
     */
    private boolean cabinetCheck(@NotNull Block signBlock) {
        if (signBlock.getType() != Material.AIR && !Ollivanders2Common.isSign(signBlock)) {
            common.printDebugMessage("Harmonia stationary: signBlock is not air or a sign. Block type is " + signBlock.getType(), null, null, false);
            return false;
        }

        return (signBlock.getRelative(1, 0, 0).getType() != Material.AIR && signBlock.getRelative(1, 1, 0).getType() != Material.AIR
                && signBlock.getRelative(-1, 0, 0).getType() != Material.AIR && signBlock.getRelative(-1, 1, 0).getType() != Material.AIR
                && signBlock.getRelative(0, 0, 1).getType() != Material.AIR && signBlock.getRelative(0, 1, 1).getType() != Material.AIR
                && signBlock.getRelative(0, 0, -1).getType() != Material.AIR && signBlock.getRelative(0, 1, -1).getType() != Material.AIR
                && signBlock.getRelative(0, 1, 0).getType() == Material.AIR && signBlock.getRelative(0, 2, 0).getType() != Material.AIR);
    }

    /**
     * @return the serialized twin cabinet location; empty if it could not be serialized
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
     * Restore {@link #twinCabinetLocation} from saved data, leaving it unchanged if absent.
     *
     * @param spellData the serialized spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
        Location loc = common.deserializeLocation(spellData, twinLabel);

        if (loc != null)
            twinCabinetLocation = loc;
    }

    /**
     * Teleport a player who steps into this cabinet to the twin, putting them on cooldown. Kills this cabinet if the
     * twin no longer exists; skips players who arrived via the twin and are still on its cooldown.
     *
     * @param event the player move event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isUsing(player))
            return;

        Location toLoc = event.getTo();
        if (toLoc == null || !isLocationInside(toLoc))
            return;

        // ignore head turns and other non-positional moves
        if (Ollivanders2Common.locationEquals(toLoc, event.getFrom()))
            return;

        HARMONIA_NECTERE_PASSUS twin = getTwin();
        if (twin == null) {
            kill();
            return;
        }
        else if (twin.isUsing(player)) {
            // player just arrived from the twin; teleporting now would bounce them straight back
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

    @Override
    void doCleanUp() {
    }

    /**
     * @return true if the caster UUID, cabinet location, and twin cabinet location are all present
     */
    @Override
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null && twinCabinetLocation != null;
    }
}

package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.stationaryspell.events.FlooNetworkEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.Effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Aliquam Floo: a permanent stationary spell that makes a fireplace a Floo Network node. A player throws Floo powder
 * into it to open a 30-second window, then speaks a destination name to be teleported to that node; an unrecognized
 * name sends them to a random node instead. All active nodes are held in a static network registry.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Floo_Network">Harry Potter Wiki - Floo Network</a>
 */
public class ALIQUAM_FLOO extends O2StationarySpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 2;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 2;

    /**
     * Duration bound, unused because Aliquam Floo is permanent.
     */
    public static final int minDurationConfig = 1000;

    /**
     * Duration bound, unused because Aliquam Floo is permanent.
     */
    public static final int maxDurationConfig = 1000;

    /**
     * Message sent to the player when a floo teleport succeeds.
     */
    public static final String successMessage = "Fire swirls around you.";

    /**
     * Message sent to the player when a floo teleport is cancelled.
     */
    public static final String cancelledMessage = "Nothing seems to happen.";

    /**
     * The unique name of this floo network location (for teleportation destinations).
     */
    private String flooName;

    /**
     * Countdown timer (in ticks) for the active floo window after activation.
     * When positive, the floo is active and players can speak destinations to teleport.
     */
    private int cooldown = 0;

    /**
     * Serialization key for the floo location name.
     */
    private final String flooNameLabel = "name";

    /**
     * Static list of all registered ALIQUAM_FLOO network locations.
     * Used to lookup destinations when players speak location names and for random destination fallback.
     */
    private final static List<ALIQUAM_FLOO> flooNetworkLocations = new ArrayList<>();

    /**
     * Map of pending floo network teleportation events, keyed by player UUID.
     * Tracks active teleportation attempts for each player.
     */
    final HashMap<UUID, FlooNetworkEvent> flooNetworkEvents = new HashMap<>();

    /**
     * Whether to use soul fire visual effect (true) or legacy mob spawner flames (false).
     * Configured from plugin config file.
     */
    private boolean soulFireFlooEffect = false;

    /**
     * The original block material of the fireplace (either regular FIRE or SOUL_FIRE).
     * Used to restore the original fire type when the floo deactivates.
     */
    private Material fireType = Material.FIRE;

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public ALIQUAM_FLOO(@NotNull Ollivanders2 plugin) {
        super(plugin);
        spellType = O2StationarySpellType.ALIQUAM_FLOO;

        permanent = true;
        radius = minRadius;
        loadFlooConfig();
    }

    /**
     * Constructor for casting a new Aliquam Floo spell, registering this fireplace as a floo node named {@code flooName}.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the fireplace
     * @param flooName the unique name for this floo network location
     */
    public ALIQUAM_FLOO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull String flooName) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.ALIQUAM_FLOO;
        permanent = true;
        radius = minRadius;
        loadFlooConfig();
        this.flooName = flooName;
        fireType = location.getBlock().getType();

        flooNetworkLocations.add(this);
        common.printDebugMessage("Creating aliquam floo stationary spell with a floo name of " + flooName, null, null, false);
    }

    /**
     * Read the {@code soulFireFlooEffect} config setting that selects the visual effect (soul fire vs. mob-spawner
     * flames).
     */
    void loadFlooConfig() {
        if (p.getConfig().isSet("soulFireFlooEffect"))
            soulFireFlooEffect = p.getConfig().getBoolean("soulFireFlooEffect");
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Set the location and capture the fireplace's current block type so it can be restored later.
     *
     * @param location the center location of the fireplace
     */
    @Override
    void setLocation(@NotNull Location location) {
        super.setLocation(location);
        fireType = location.getBlock().getType();
    }

    /**
     * Kill this spell and remove it from the floo network registry.
     */
    @Override
    public void kill() {
        super.kill();
        flooNetworkLocations.remove(this);
    }

    /**
     * While active, count down the window and refresh the fire effect; while inactive, watch for floo powder thrown
     * into the fireplace and activate on it.
     */
    @Override
    public void upkeep() {
        if (isWorking()) {
            cooldown = cooldown - 1;

            if ((cooldown % 10) == 0)
                turnOnFlooFireEffect();

            if (cooldown <= 0) {
                stopWorking();
                common.printDebugMessage("Turning off floo " + flooName, null, null, false);
            }
        }
        else {
            for (Item item : EntityCommon.getItemsInRadius(location, 1)) {
                if (!O2ItemType.FLOO_POWDER.isItemThisType(item))
                    continue;

                item.remove();
                turnOnFlooFireEffect();

                common.printDebugMessage("Turning on floo " + flooName, null, null, false);

                cooldown = Ollivanders2Common.ticksPerSecond * 30;
            }
        }
    }

    /**
     * Show the active-floo visual effect: recolor the fireplace to soul fire if that effect is enabled, otherwise play
     * mob-spawner flame particles.
     */
    private void turnOnFlooFireEffect() {
        if (soulFireFlooEffect) {
            Block block = location.getBlock();
            if (fireType == Material.CAMPFIRE) {
                block.setType(Material.SOUL_CAMPFIRE);
            }
            else {
                // the block below must become soul sand or the soul fire won't stay lit
                Block fireBase = block.getRelative(BlockFace.DOWN);

                fireBase.setType(Material.SOUL_SAND);
                block.setType(Material.SOUL_FIRE);
            }
        }
        else {
            world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
        }
    }

    /**
     * Get the name of this floo location.
     *
     * @return the floo name
     */
    public String getFlooName() {
        return flooName;
    }

    /**
     * Whether this floo is currently active and accepting a spoken destination.
     *
     * @return true if within the activation window, false otherwise
     */
    public boolean isWorking() {
        return cooldown > 0;
    }

    /**
     * Deactivate this floo and revert the soul-fire visual effect back to the original fire.
     */
    public void stopWorking() {
        cooldown = 0;

        Block block = location.getBlock();

        if (soulFireFlooEffect) {
            if (fireType == Material.CAMPFIRE) {
                block.setType(Material.CAMPFIRE);
            }
            else {
                // restore the block below so the ordinary fire will stay lit
                Block fireBase = block.getRelative(BlockFace.DOWN);

                fireBase.setType(Material.NETHERRACK);
                block.setType(Material.FIRE);
            }
        }
    }

    /**
     * @return the serialized spell data, holding this floo's name
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        Map<String, String> spellData = new HashMap<>();

        spellData.put(flooNameLabel, flooName);

        return spellData;
    }

    /**
     * Restore this floo's name from saved data.
     *
     * @param spellData the map of saved spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
        flooName = spellData.get(flooNameLabel);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        if (active && !flooNetworkLocations.contains(this)) {
            flooNetworkLocations.add(this);
        }
    }

    /**
     * When a player speaks inside an active floo, treat their message as a destination name: teleport them to the
     * matching node, or to a random node if the name is unknown.
     *
     * @param event the async player chat event
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chat = event.getMessage();

        if (flooNetworkLocations.size() <= 1 || !isLocationInside(player.getLocation()) || !isWorking())
            return;

        ALIQUAM_FLOO destination = null;

        for (ALIQUAM_FLOO floo : flooNetworkLocations) {
            if (floo.getFlooName().equalsIgnoreCase(chat.trim()))
                destination = floo;
        }

        if (destination == null) {
            int randomIndex = Math.abs(Ollivanders2Common.random.nextInt() % flooNetworkLocations.size());
            destination = flooNetworkLocations.get(randomIndex);
        }

        Ollivanders2Common.chatDropoff(event.getRecipients(), Ollivanders2.chatDropoff, player.getLocation());

        FlooNetworkEvent flooNetworkEvent = new FlooNetworkEvent(player, destination);
        flooNetworkEvents.put(player.getUniqueId(), flooNetworkEvent);

        // delayed so other listeners can cancel the FlooNetworkEvent before the teleport happens
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.isCancelled())
                    doFlooTeleportEvent(player);
                else
                    flooNetworkEvents.remove(player.getUniqueId());
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Fire the pending {@link FlooNetworkEvent} for a player, then teleport them (with a success message) unless a
     * listener cancelled it, and deactivate this floo.
     *
     * @param player the player to teleport
     */
    private void doFlooTeleportEvent(Player player) {
        FlooNetworkEvent flooNetworkEvent = flooNetworkEvents.get(player.getUniqueId());

        if (flooNetworkEvent == null)
            return;

        p.getServer().getPluginManager().callEvent(flooNetworkEvent);

        new BukkitRunnable() {
            @Override
            public void run() {
                FlooNetworkEvent flooNetworkEvent = flooNetworkEvents.get(player.getUniqueId());
                if (flooNetworkEvent == null)
                    return;

                if (!flooNetworkEvent.isCancelled()) {
                    p.addTeleportAction(player, flooNetworkEvent.getDestination());
                    player.sendMessage(Ollivanders2.chatColor + successMessage);
                }
                else
                    player.sendMessage(Ollivanders2.chatColor + cancelledMessage);

                stopWorking();
                flooNetworkEvents.remove(player.getUniqueId());
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Cancel combustion of an entity inside the fireplace area.
     *
     * @param event the entity combust event
     */
    @Override
    void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {
        Entity entity = event.getEntity();
        Location entityLocation = entity.getLocation();

        if (isLocationInside(entityLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("ALIQUAM_FLOO: canceled EntityCombustEvent", null, null, false);
        }
    }

    /**
     * Cancel fire damage (campfire, fire, fire tick) to an entity inside the fireplace area.
     *
     * @param event the entity damage event
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Location entityLocation = entity.getLocation();

        if (event.getCause() == EntityDamageEvent.DamageCause.CAMPFIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if (isLocationInside(entityLocation)) {
                event.setCancelled(true);
                common.printDebugMessage("ALIQUAM_FLOO: canceled EntityDamageEvent", null, null, false);
            }
        }
    }

    /**
     * Deactivate this floo, restoring the fireplace to its normal state.
     */
    @Override
    void doCleanUp() {
        stopWorking();
    }

    /**
     * @return true if the caster UUID, location, and a non-empty floo name are all present
     */
    @Override
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null && flooName != null && !flooName.isEmpty();
    }

    /**
     * Get the names of every registered floo node, i.e. the valid teleport destinations.
     *
     * @return the floo node names; empty if none
     */
    @NotNull
    public static List<String> getFlooFireplaceNames() {
        ArrayList<String> names = new ArrayList<>();
        for (ALIQUAM_FLOO aliquamFloo : flooNetworkLocations) {
            names.add(aliquamFloo.getFlooName());
        }
        return names;
    }

    /**
     * Clear the floo network registry and remove every active Aliquam Floo spell, e.g. on shutdown or reload.
     */
    public static void clearFlooNetwork() {
        flooNetworkLocations.clear();

        // clean up any active floo stationary spells
        List<O2StationarySpell> stationarySpells = Ollivanders2API.getStationarySpells().getActiveStationarySpells();

        for (O2StationarySpell stationarySpell : stationarySpells) {
            if (stationarySpell.spellType == O2StationarySpellType.ALIQUAM_FLOO) {
                Ollivanders2API.getStationarySpells().removeStationarySpell(stationarySpell);
            }
        }
    }
}

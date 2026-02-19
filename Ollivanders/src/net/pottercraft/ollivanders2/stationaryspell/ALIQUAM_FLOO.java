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
import org.bukkit.World;
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
 * A stationary spell that transforms a fireplace into a Floo Network connection point.
 *
 * <p>The Aliquam Floo spell enables magical teleportation between registered floo network locations.
 * Players throw floo powder into the fireplace to activate it, then speak a destination name to teleport.
 * If the destination name is not recognized, the player is randomly sent to another floo location.</p>
 *
 * <p>Spell characteristics:
 * <ul>
 *   <li>Fixed radius of 2 blocks (hardcoded, cannot be modified)</li>
 *   <li>Permanent spell (never expires unless killed)</li>
 *   <li>Activates when floo powder is thrown into the center fireplace block</li>
 *   <li>30-second active window for player to speak destination and teleport</li>
 *   <li>Visual effects: legacy mob spawner flames or soul fire (configurable)</li>
 *   <li>Prevents fire and combustion damage within the protected area</li>
 * </ul>
 * </p>
 *
 * <p>All active floo locations are registered in a static network list for teleportation discovery.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Floo_Network">https://harrypotter.fandom.com/wiki/Floo_Network</a>
 */
public class ALIQUAM_FLOO extends O2StationarySpell {
    /**
     * Minimum spell radius (2 blocks, hardcoded and cannot be changed).
     */
    public static final int minRadiusConfig = 2;

    /**
     * Maximum spell radius (2 blocks, hardcoded and cannot be changed).
     */
    public static final int maxRadiusConfig = 2;

    /**
     * Minimum spell duration in ticks (not used - ALIQUAM_FLOO is permanent).
     */
    public static final int minDurationConfig = 1000;

    /**
     * Maximum spell duration in ticks (not used - ALIQUAM_FLOO is permanent).
     */
    public static final int maxDurationConfig = 1000;

    /**
     * Message sent to player when the floo event happens successfully
     */
    public static final String successMessage = "Fire swirls around you.";

    /**
     * Message sent to player when the floo event is cancelled
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
     * Constructs an ALIQUAM_FLOO spell from deserialized data at server startup.
     *
     * <p>Used only for loading saved spells from disk. Registers this floo location in the network
     * and loads visual effect configuration. Do not use to cast new spells - use the full constructor instead.</p>
     *
     * @param plugin a callback to the MC plugin
     */
    public ALIQUAM_FLOO(@NotNull Ollivanders2 plugin) {
        super(plugin);
        spellType = O2StationarySpellType.ALIQUAM_FLOO;

        permanent = true;
        radius = minRadius;
        loadFlooConfig();

        flooNetworkLocations.add(this);
    }

    /**
     * Constructs a new ALIQUAM_FLOO spell cast by a player.
     *
     * <p>Creates a new floo network location at the specified fireplace. Registers this spell in the
     * network and loads visual effect configuration.</p>
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
        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Loads floo network configuration from the plugin config file.
     *
     * <p>Reads the soulFireFlooEffect setting to determine whether to use soul fire
     * or legacy mob spawner flames for visual effects. This is called during spell construction
     * to initialize visual effect preferences.</p>
     */
    void loadFlooConfig() {
        if (p.getConfig().isSet("soulFireFlooEffect"))
            soulFireFlooEffect = p.getConfig().getBoolean("soulFireFlooEffect");
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets fixed values: radius is always 2 blocks, duration constraints are ignored
     * since ALIQUAM_FLOO is permanent.</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig; // not used - aliquam floo is permanent
        maxDuration = maxDurationConfig; // not used - aliquam floo is permanent
    }

    /**
     * Sets the location of this floo fireplace and captures the original block type.
     *
     * <p>When a location is set (during spell initialization), captures the current block type
     * at that location so it can be restored later when the spell deactivates or is destroyed.</p>
     *
     * @param location the center location of the fireplace (not null)
     */
    @Override
    void setLocation(@NotNull Location location) {
        this.location = location;
        fireType = location.getBlock().getType(); // get the block type in the location
    }

    /**
     * Kills this floo spell and removes it from the network.
     *
     * <p>Called when the spell is destroyed or at server shutdown. Removes this location from
     * the global floo network list and performs standard spell cleanup.</p>
     */
    @Override
    public void kill() {
        super.kill();
        flooNetworkLocations.remove(this);
    }

    /**
     * Performs per-tick upkeep for this floo spell.
     *
     * <p>When active, decrements the cooldown timer and periodically shows fire effects.
     * When inactive, checks for floo powder being thrown into the fireplace to activate it.</p>
     */
    @Override
    public void upkeep() {
        // if this fireplace is already active,
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
     * Displays the visual effect when the floo network becomes active.
     *
     * <p>If soul fire effect is enabled, transforms the fireplace blocks to soul fire.
     * Otherwise, plays mob spawner flame particles at the fireplace location.</p>
     */
    private void turnOnFlooFireEffect() {
        if (soulFireFlooEffect) {
            Block block = location.getBlock();
            // turn the flame in to blue flame
            if (fireType == Material.CAMPFIRE) {
                block.setType(Material.SOUL_CAMPFIRE);
            }
            else {
                // we have to change the block underneath or the fire won't stay lit
                Block fireBase = block.getRelative(BlockFace.DOWN);

                fireBase.setType(Material.SOUL_SAND);
                block.setType(Material.SOUL_FIRE);
            }
        }
        else {
            World world = location.getWorld();
            if (world == null) {
                kill();
                return;
            }

            world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
        }
    }

    /**
     * Get the name of this floo location
     *
     * @return the name of this floo location
     */
    public String getFlooName() {
        return flooName;
    }

    /**
     * Checks if this floo location is currently active and accepting teleportation requests.
     *
     * @return true if the floo is active (within the 30-second activation window), false otherwise
     */
    public boolean isWorking() {
        return cooldown > 0;
    }

    /**
     * Deactivates the floo network connection and restores the original fireplace appearance.
     *
     * <p>Resets the cooldown timer and reverses any visual effects (converts soul fire back to
     * regular fire or removes particle effects).</p>
     */
    public void stopWorking() {
        cooldown = 0;

        Block block = location.getBlock();

        if (soulFireFlooEffect) {
            if (fireType == Material.CAMPFIRE) {
                block.setType(Material.CAMPFIRE);
            }
            else {
                // we have to change the block underneath or the fire won't stay lit
                Block fireBase = block.getRelative(BlockFace.DOWN);

                fireBase.setType(Material.NETHERRACK);
                block.setType(Material.FIRE);
            }
        }
    }

    /**
     * Serializes the floo location name for persistence across server restarts.
     *
     * @return a map containing the serialized floo name
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        Map<String, String> spellData = new HashMap<>();

        spellData.put(flooNameLabel, flooName);

        return spellData;
    }

    /**
     * Deserializes and restores the floo location name from saved data.
     *
     * @param spellData the map of saved spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
        flooName = spellData.get(flooNameLabel);
    }

    /**
     * Handles player chat within an active floo network location.
     *
     * <p>When a player speaks while the floo is active, their message is interpreted as a floo
     * network destination. Looks up the destination in the network and initiates a teleportation
     * event. If the destination is not found, randomly selects another floo location.</p>
     *
     * @param event the async player chat event
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer(); // will never be null
        String chat = event.getMessage(); // will never be null

        // player is not in the location or the fireplace is not currently activated
        if (flooNetworkLocations.size() <= 1 || !isLocationInside(player.getLocation()) || !isWorking())
            return;

        // look for the destination in the registered floo network
        ALIQUAM_FLOO destination = null;

        for (ALIQUAM_FLOO floo : flooNetworkLocations) {
            if (floo.getFlooName().equalsIgnoreCase(chat.trim()))
                destination = floo;
        }

        // if that destination doesn't exist, pick a random destination
        if (destination == null) {
            int randomIndex = Math.abs(Ollivanders2Common.random.nextInt() % flooNetworkLocations.size());
            destination = flooNetworkLocations.get(randomIndex);
        }

        // dropoff chat
        Ollivanders2Common.chatDropoff(event.getRecipients(), Ollivanders2.chatDropoff, player.getLocation());

        // create and fire the floo network event
        FlooNetworkEvent flooNetworkEvent = new FlooNetworkEvent(player, destination);
        flooNetworkEvents.put(player.getUniqueId(), flooNetworkEvent);

        // do the teleport if the event isn't canceled by something else
        // run this at a delay to give all other event listeners time to react
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
     * Executes the floo network teleportation for a player.
     *
     * <p>Calls the FlooNetworkEvent for listeners to process, then schedules the actual teleportation.
     * Sends appropriate messages to the player and deactivates the floo after teleportation.</p>
     *
     * @param player the player to teleport via the floo network
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
     * Prevents entities from combusting due to the fireplace.
     *
     * <p>Cancels combustion events for entities within the spell radius to prevent
     * accidental damage in the magical floo network fireplace.</p>
     *
     * @param event the entity combust event
     */
    @Override
    void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {
        Entity entity = event.getEntity(); // will never be null
        Location entityLocation = entity.getLocation(); // will never be null

        if (isLocationInside(entityLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("ALIQUAM_FLOO: canceled EntityCombustEvent", null, null, false);
        }
    }

    /**
     * Prevents fire and combustion damage within the protected fireplace area.
     *
     * <p>Cancels fire-related damage events (CAMPFIRE, FIRE, FIRE_TICK) for entities
     * inside the spell radius, ensuring players are protected while using the floo network.</p>
     *
     * @param event the entity damage event
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity(); // will never be null
        Location entityLocation = entity.getLocation(); // will never be null

        if (event.getCause() == EntityDamageEvent.DamageCause.CAMPFIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if (isLocationInside(entityLocation)) {
                event.setCancelled(true);
                common.printDebugMessage("ALIQUAM_FLOO: canceled EntityDamageEvent", null, null, false);
            }
        }
    }

    /**
     * Cleans up when this floo spell ends.
     *
     * <p>Deactivates the floo network connection, restoring the fireplace to its normal state.</p>
     */
    @Override
    void doCleanUp() {
        stopWorking();
    }

    /**
     * Checks if this spell has been properly deserialized with required data.
     *
     * <p>Verifies that the spell has caster UUID, location, and a non-empty floo name set,
     * which are all required for a floo network location to function.</p>
     *
     * @return true if all required data is present and valid, false otherwise
     */
    @Override
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null && flooName != null && !flooName.isEmpty();
    }

    /**
     * Gets the names of all registered floo network locations.
     *
     * <p>Returns a list of unique names for all active floo fireplace locations in the network.
     * Used for displaying available teleportation destinations to players.</p>
     *
     * @return a list of all registered floo fireplace names (not null, may be empty)
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
     * Clears all floo network locations and removes active floo spells from the world.
     *
     * <p>Called during server shutdown or plugin reload. Clears the network location list and
     * removes all active ALIQUAM_FLOO spell instances from the spell manager.</p>
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

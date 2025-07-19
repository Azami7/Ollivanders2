package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

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
 * Makes a "fireplace" a floo network location
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.ALIQUAM_FLOO}
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Floo_Network">https://harrypotter.fandom.com/wiki/Floo_Network</a>
 */
public class ALIQUAM_FLOO extends O2StationarySpell {
    /**
     * Radius for this spell is always 4
     */
    public static final int minRadiusConfig = 4;

    /**
     * Radius for this spell is always 4
     */
    public static final int maxRadiusConfig = 4;

    /**
     * The name of this floo location
     */
    private String flooName;

    /**
     * The cooldown between uses so that we don't accidentally teleport more than one player or teleport someone as soon as they arrive at a location
     */
    private int cooldown = 0;

    /**
     * The label for the floo location for serializing this stationary spell
     */
    private final String flooNameLabel = "name";

    /**
     * A static list of all floo locations to be checked when a player says a location to teleport to
     */
    public static List<ALIQUAM_FLOO> flooNetworkLocations = new ArrayList<>();

    /**
     * All the pending floo network events
     */
    final HashMap<UUID, FlooNetworkEvent> flooNetworkEvents = new HashMap<>();

    /**
     * Whether we use the legacy floo effect or soul fire
     */
    private boolean soulFireFlooEffect = false;

    /**
     * What type of fire this floo spell is on
     */
    private Material fireType = Material.FIRE;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public ALIQUAM_FLOO(@NotNull Ollivanders2 plugin) {
        super(plugin);

        init();
    }

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param flooName the name of this floo location
     */
    public ALIQUAM_FLOO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull String flooName) {
        super(plugin, pid, location);

        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        this.flooName = flooName;

        init();

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Common constructor steps
     */
    private void init() {
        spellType = O2StationarySpellType.ALIQUAM_FLOO;

        if (p.getConfig().isSet("soulFireFlooEffect"))
            soulFireFlooEffect = p.getConfig().getBoolean("soulFireFlooEffect");

        this.radius = minRadius = maxRadius = minRadiusConfig;
        permanent = true;

        flooNetworkLocations.add(this);
    }

    /**
     * This kills the floo stationary spell
     */
    @Override
    public void kill() {
        super.kill();
        flooNetworkLocations.remove(this);
    }

    /**
     * Check for players activating the floo
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
            for (Item item : EntityCommon.getItems(location, 1)) {
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
     * Play effect that shows the fireplace is active
     */
    private void turnOnFlooFireEffect() {
        if (soulFireFlooEffect) {
            Block block = location.getBlock();
            // need to keep track of what type of fire this is
            fireType = block.getType();

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
     * Is it accepting floo destinations?
     *
     * @return true if this floo destination is online, false otherwise
     */
    public boolean isWorking() {
        return cooldown > 0;
    }

    /**
     * Stop the floo fireplace working after teleporting.
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
     * Serialize all data specific to this spell so it can be saved.
     *
     * @return a map of the serialized data
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        Map<String, String> spellData = new HashMap<>();

        spellData.put(flooNameLabel, flooName);

        return spellData;
    }

    /**
     * Deserialize the data for this spell and load the data to this spell.
     *
     * @param spellData a map of the saved spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
        for (Entry<String, String> e : spellData.entrySet()) {
            if (e.getKey().equals(flooNameLabel))
                flooName = e.getValue();
        }
    }

    /**
     * Handle player floo chat
     *
     * @param event the event
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer(); // will never be null
        String chat = event.getMessage(); // will never be null

        if (!(player.getLocation().getBlock().equals(getBlock())) || !isWorking())
            return;

        // look for the destination in the registered floo network
        ALIQUAM_FLOO destination = null;

        for (ALIQUAM_FLOO floo : flooNetworkLocations) {
            if (floo.getFlooName().equalsIgnoreCase(chat.trim()))
                destination = floo;
        }

        if (destination == null) {
            int randomIndex = Math.abs(Ollivanders2Common.random.nextInt() % flooNetworkLocations.size());
            destination = flooNetworkLocations.get(randomIndex);

            Ollivanders2Common.chatDropoff(event.getRecipients(), Ollivanders2.chatDropoff, player.getLocation());
        }

        FlooNetworkEvent flooNetworkEvent = new FlooNetworkEvent(player, destination);
        flooNetworkEvents.put(player.getUniqueId(), flooNetworkEvent);

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
     * Do the floo travel event.
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
                    player.sendMessage(Ollivanders2.chatColor + "Fire swirls around you.");
                }
                else
                    player.sendMessage(Ollivanders2.chatColor + "Nothing seems to happen.");

                stopWorking();
                flooNetworkEvents.remove(player.getUniqueId());
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Handle entity combusting due to the fire block
     *
     * @param event the event
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
     * Dont take damage in the fireplace
     *
     * @param event the event
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity(); // will never be null
        Location entityLocation = entity.getLocation(); // will never be null

        if (isLocationInside(entityLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("ALIQUAM_FLOO: canceled EntityDamageEvent", null, null, false);
        }
    }

    @Override
    void doCleanUp() {
        stopWorking();
    }
}

package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * A stationary spell that creates a horcrux - an anchor point for player resurrection with preserved power.
 *
 * <p>Horcrux creates a permanent magical anchor that allows the spell's caster to respawn at the
 * horcrux location when killed, while retaining all spell levels and experience. The spell:
 * <ul>
 *   <li>Creates an anchor point as a physical item in the world</li>
 *   <li>Prevents the player from losing spell levels on death</li>
 *   <li>Prevents the player from losing experience on death</li>
 *   <li>Respawns the player at the horcrux location</li>
 *   <li>Applies debilitative effects (blindness, wither) to players near the horcrux</li>
 *   <li>Protects the horcrux item from being picked up or despawning</li>
 *   <li>Can only be destroyed by fiendfyre</li>
 * </ul>
 * </p>
 *
 * <p>The spell is permanent and persists across server restarts.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Horcrux-making_spell">https://harrypotter.fandom.com/wiki/Horcrux-making_spell</a>
 * @since 2.21
 */
public class HORCRUX extends O2StationarySpell {
    /**
     * The message players see on death
     */
    public static final String deathMessage = "Death approaches but cannot claim your soul.";

    /**
     * Minimum spell radius (3 blocks) - effect range for debilitation effects.
     */
    public static final int minRadiusConfig = 3;

    /**
     * Maximum spell radius (3 blocks) - effect range for debilitation effects.
     */
    public static final int maxRadiusConfig = 3;

    /**
     * Minimum spell duration (1000 ticks) - not used, horcrux is permanent.
     */
    public static final int minDurationConfig = 1000;

    /**
     * Maximum spell duration (1000 ticks) - not used, horcrux is permanent.
     */
    public static final int maxDurationConfig = 1000;

    /**
     * The material type of the horcrux item
     */
    private Material horcruxMaterial;

    /**
     * The item at this location that is the horcrux
     */
    private Item horcruxItem;

    /**
     * The name of the world this horcrux is in
     */
    private String worldName;

    /**
     * Keep track of the affected players
     */
    private final Map<UUID, Integer> affectedPlayers = new HashMap<>();

    /**
     * How long to affect someone who gets too close to the horcrux
     */
    public static final int effectDuration = Ollivanders2Common.ticksPerSecond * 15;

    private final String materialLabel = "itemType";
    private final String worldLabel = "world";

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public HORCRUX(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.HORCRUX;
        permanent = true;
        this.radius = minRadius = maxRadius = minRadiusConfig;
    }

    /**
     * Constructs a new HORCRUX spell cast by a player.
     *
     * <p>Creates a permanent horcrux anchor at the specified location using the provided item.
     * The player will respawn at this location when killed, retaining all spell levels and experience.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the horcrux anchor (not null)
     * @param item     the item representing the horcrux in the world (not null)
     */
    public HORCRUX(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull Item item) {
        super(plugin);

        spellType = O2StationarySpellType.HORCRUX;
        permanent = true;

        setPlayerID(pid);
        setLocation(location);
        radius = minRadius = maxRadius = minRadiusConfig;
        duration = 10;

        worldName = location.getWorld().getName();
        horcruxItem = item;
        horcruxMaterial = item.getItemStack().getType();

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
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
        minDuration = minDurationConfig; // not used - horcrux floo is permanent
        maxDuration = maxDurationConfig; // not used - horcrux floo is permanent
    }

    /**
     * Ages the spell and manages player debilitation effects.
     *
     * <p>Decrements the duration timers for affected players, removing them from the affected list
     * when their duration expires.</p>
     */
    @Override
    public void upkeep() {
        // decrement all the affected cooldowns by a tick
        ArrayList<UUID> iterator = new ArrayList<>(affectedPlayers.keySet());

        for (UUID playerID : iterator) {
            int duration = affectedPlayers.get(playerID) - 1;

            if (duration > 0)
                affectedPlayers.replace(playerID, duration);
            else
                affectedPlayers.remove(playerID);
        }
    }

    /**
     * Serializes the horcrux's persistent data for server restart recovery.
     *
     * <p>Saves the material type and world name so the horcrux item can be properly respawned
     * when the server restarts. This ensures the horcrux persists across server restarts.</p>
     *
     * @return a map containing the horcrux material type and world name
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        Map<String, String> spellData = new HashMap<>();

        spellData.put(materialLabel, horcruxMaterial.toString());
        spellData.put(worldLabel, worldName);

        return spellData;
    }

    /**
     * Deserializes the horcrux's persistent data when loading from server restart.
     *
     * <p>Restores the material type and world name that were saved. If the material type
     * is no longer valid (removed in a Minecraft update) or if required data is missing,
     * the spell is destroyed.</p>
     *
     * @param spellData a map containing the saved horcrux material and world name
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
        for (Map.Entry<String, String> e : spellData.entrySet()) {
            if (e.getKey().equals(materialLabel)) {
                String materialName = e.getValue();

                horcruxMaterial = Material.getMaterial(materialName);
                if (horcruxMaterial == null) {
                    common.printDebugMessage("O2StationarySpell.HORCRUX: saved material " + materialName + " not found", null, null, true);
                    kill();
                    return;
                }
            }
            else if (e.getKey().equals(worldLabel)) {
                worldName = e.getValue();
            }
        }

        if (horcruxMaterial == null || worldName == null) {
            kill();
        }
    }

    /**
     * Respawns the horcrux item on server restart when a player joins.
     *
     * <p>After a server restart, the horcrux item needs to be restored. Since the WorldLoadEvent
     * fires before plugin listeners are registered, the horcrux item is respawned the first time
     * any player joins the server. This method:
     * <ul>
     *   <li>Checks if the horcrux item already respawned naturally (by the game)</li>
     *   <li>If not found, manually respawns the horcrux item at its original location</li>
     *   <li>Marks the item as loaded so this process only happens once per restart</li>
     * </ul>
     * </p>
     *
     * @param event the player join event (not null)
     */
    @Override
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        if (!event.getPlayer().getUniqueId().equals(playerUUID))
            return;

        // is the player's horcrux item spawned?
        Item horcrux = EntityCommon.getItemAtLocation(location);

        // there is no item here or some other item is here
        if (horcrux == null || (horcrux.getItemStack().getType() != horcruxMaterial)) {
            respawnHorcruxItem();
        }
    }

    /**
     * Creates and spawns the horcrux item at the spell location.
     *
     * <p>Spawns a new item entity at the horcrux location using the saved material type.
     * The item will be protected from pickup and despawning by the spell's event handlers.</p>
     *
     * <p>If the world is invalid, logs an error and destroys the spell.</p>
     */
    private void respawnHorcruxItem() {
        World world = p.getServer().getWorld(worldName);
        if (world == null) {
            common.printDebugMessage("O2StationarySpell.HORCRUX: world " + worldName + " is null", null, null, true);
            kill();
            return;
        }

        ItemStack itemStack = new ItemStack(horcruxMaterial);
        itemStack.setAmount(1);

        common.printDebugMessage("O2StationarySpell.HORCRUX: creating horcrux for player " + playerUUID, null, null, false);
        horcruxItem = world.dropItem(location, itemStack);
    }

    /**
     * Protects the horcrux caster from losing progress on death.
     *
     * <p>When the player who created the horcrux dies, they are protected from the normal
     * consequences of death:
     * <ul>
     *   <li>Inventory items are preserved (not dropped)</li>
     *   <li>Experience level is preserved</li>
     *   <li>Total experience points are preserved</li>
     * </ul>
     * The death message is customized to reflect the magical protection of the horcrux.</p>
     *
     * @param event the player death event (not null)
     */
    @Override
    void doOnPlayerDeathEvent(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.setRespawnLocation(location, true);

        if (player.getUniqueId().equals(playerUUID)) {
            event.setDeathMessage(deathMessage);
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.setNewTotalExp(player.getTotalExperience());
        }
    }

    /**
     * Applies debilitative effects to players who enter the horcrux area.
     *
     * <p>When a player (other than the horcrux caster) enters the spell radius:
     * <ul>
     *   <li>Applies blindness effect (amplifier level 2) for 200 ticks</li>
     *   <li>Applies wither effect (amplifier level 1) for 200 ticks</li>
     *   <li>Tracks the player to avoid reapplying effects until the cooldown expires</li>
     * </ul>
     * The horcrux caster is immune to these effects.</p>
     *
     * @param event the player move event (not null)
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLocation = event.getTo();
        if (toLocation == null || !isLocationInside(toLocation))
            return;

        Player target = event.getPlayer();

        // don't affect the creator of the horcrux
        if (target.getUniqueId().equals(playerUUID)) {
            common.printDebugMessage("HORCRUX: " + target.getName() + " enter their own horcrux area", null, null, false);
            return;
        }

        // don't affect players already affected
        if (affectedPlayers.containsKey(target.getUniqueId()))
            return;

        // add blindness and wither effects to player
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, effectDuration, 1));

        // add affected player to list so we don't keep re-adding these effects every time they move
        affectedPlayers.put(target.getUniqueId(), effectDuration);
    }

    /**
     * Prevents the horcrux item from despawning naturally.
     *
     * <p>Minecraft normally despawns dropped items after 5 minutes. This event handler
     * ensures the horcrux item never despawns, protecting it from natural cleanup.</p>
     *
     * @param event the item despawn event (not null)
     */
    @Override
    void doOnItemDespawnEvent(@NotNull ItemDespawnEvent event) {
        if (horcruxItem == null) //this can happen on restart when things are not fully loaded yet
            return;

        Item eventItem = event.getEntity();

        if (eventItem.getUniqueId().equals(horcruxItem.getUniqueId()))
            event.setCancelled(true);
    }

    /**
     * Prevents entities from picking up the horcrux item.
     *
     * <p>Protects the horcrux item from being collected by mobs or players. The item
     * remains untouchable at its location.</p>
     *
     * @param event the entity pickup item event (not null)
     */
    @Override
    void doOnEntityPickupItemEvent(@NotNull EntityPickupItemEvent event) {
        if (horcruxItem == null) //this can happen on restart when things are not fully loaded yet
            return;

        Item eventItem = event.getItem();

        if (eventItem.getUniqueId().equals(horcruxItem.getUniqueId()))
            event.setCancelled(true);
    }

    /**
     * Prevents hoppers and other inventory collectors from picking up the horcrux item.
     *
     * <p>Protects the horcrux item from being collected by hoppers, minecarts with hoppers,
     * or other block inventory systems. The item cannot be moved or collected by any means.</p>
     *
     * @param event the inventory pickup item event (not null)
     */
    @Override
    void doOnInventoryItemPickupEvent(@NotNull InventoryPickupItemEvent event) {
        if (horcruxItem == null) //this can happen on restart when things are not fully loaded yet
            return;

        Item eventItem = event.getItem();

        if (eventItem.getUniqueId().equals(horcruxItem.getUniqueId()))
            event.setCancelled(true);
    }

    /**
     * Cleans up when the horcrux spell ends.
     *
     * <p>The horcrux spell requires no special cleanup on termination. The spell is permanent
     * and only ends if explicitly destroyed through other means.</p>
     */
    @Override
    void doCleanUp() {
    }

    /**
     * Validates that the spell was properly deserialized with all required data.
     *
     * <p>Checks that the following essential data was restored from the saved state:
     * <ul>
     *   <li>Player UUID (caster identification)</li>
     *   <li>Location (horcrux position)</li>
     *   <li>Horcrux material (item type)</li>
     * </ul>
     * If any required data is missing, the spell is invalid and should be destroyed.</p>
     *
     * @return true if all required data is present, false otherwise
     */
    @Override
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null && horcruxMaterial != null;
    }

    /**
     * Is this player currently affected by the horcrux?
     *
     * @param player the player to check
     * @return true if they are affected, false otherwise
     */
    public boolean isAffected(Player player) {
        return affectedPlayers.containsKey(player.getUniqueId());
    }
}
package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.ollivanders2.common.EntityCommon;
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
 * Player will spawn here when killed, with all of their spell levels intact. Only fiendfyre can destroy it.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Horcrux-making_spell">https://harrypotter.fandom.com/wiki/Horcrux-making_spell</a>
 * {@link net.pottercraft.ollivanders2.spell.ET_INTERFICIAM_ANIMAM_LIGAVERIS}
 */
public class HORCRUX extends O2StationarySpell {
    /**
     * the min radius for this spell
     */
    public static final int minRadiusConfig = 3;

    /**
     * the max radius for this spell
     */
    public static final int maxRadiusConfig = 3;

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
    private final Map<String, Integer> affectedPlayers = new HashMap<>();

    /**
     * How long to affect someone who gets too close to the horcrux
     */
    private final int effectDuration = 200;

    /**
     * Was the horcrux item loaded (for use on restart)
     */
    private boolean itemLoaded = false;

    //
    // save data labels
    //
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
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
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
        itemLoaded = true;

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Upkeep for the spell
     */
    @Override
    public void checkEffect() {
        // decrement all the affected cooldowns by a tick
        ArrayList<String> iterator = new ArrayList<>(affectedPlayers.keySet());

        for (String playerName : iterator) {
            int duration = affectedPlayers.get(playerName) - 1;

            if (duration > 0)
                affectedPlayers.replace(playerName, duration);
            else
                affectedPlayers.remove(playerName);
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

        spellData.put(materialLabel, horcruxMaterial.toString());
        spellData.put(worldLabel, worldName);

        return spellData;
    }

    /**
     * Deserialize the data for this spell and load the data to this spell.
     *
     * @param spellData a map of the saved spell data
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
     * Respawn the horcrux item after a server start and the world is loaded. Ideally we'd use the WorldLoadEvent, but it
     * seems to happen before the plugin listeners are registered (tried it, never fires) so load it the first time a
     * player joins.
     *
     * @param event the world load event
     */
    @Override
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        if (!itemLoaded) {
            // did the game respawn the item?
            for (Item itemsAtLocation : EntityCommon.getItems(location, 1)) {
                if (itemsAtLocation.getItemStack().getType() == horcruxMaterial) {
                    horcruxItem = itemsAtLocation;
                    return;
                }
            }

            // if not, respawn horcrux
            respawnHorcruxItem();

            itemLoaded = true;
        }
    }

    /**
     * Respawn the horcrux item
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
     * On player death, player keeps inventory, level, and experience.
     *
     * @param event the event
     */
    @Override
    void doOnPlayerDeathEvent(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getUniqueId() == playerUUID) {
            event.setDeathMessage("Death approaches but cannot claim your split soul.");
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.setNewTotalExp(player.getTotalExperience());
        }
    }

    /**
     * Affect players that get too close to the horcrux
     *
     * @param event the event
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
        if (affectedPlayers.containsKey(target.getName()))
            return;

        // add blindness and wither effects to player
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, effectDuration, 1));

        // add affected player to list so we keep re-adding these effects every time they move
        affectedPlayers.put(target.getName(), effectDuration);
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    @Override
    void doOnItemDespawnEvent(@NotNull ItemDespawnEvent event) {
        Item eventItem = event.getEntity();

        if (eventItem.getUniqueId() == horcruxItem.getUniqueId())
            event.setCancelled(true);
    }

    /**
     * Handle items being picked up by entities
     *
     * @param event the event
     */
    @Override
    void doOnEntityPickupItemEvent(@NotNull EntityPickupItemEvent event) {
        Item eventItem = event.getItem();

        if (eventItem.getUniqueId() == horcruxItem.getUniqueId())
            event.setCancelled(true);
    }

    /**
     * Handle items being picked up by things like hoppers
     *
     * @param event the event
     */
    @Override
    void doOnInventoryItemPickupEvent (@NotNull InventoryPickupItemEvent event ) {
        Item eventItem = event.getItem();

        if (eventItem.getUniqueId() == horcruxItem.getUniqueId())
            event.setCancelled(true);
    }

    @Override
    void doCleanUp() { }
}
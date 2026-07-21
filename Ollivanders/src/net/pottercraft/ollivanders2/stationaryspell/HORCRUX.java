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
 * Horcrux: a permanent stationary spell anchored to an item in the world. Its caster keeps their inventory,
 * experience, and level on death and respawns at the horcrux; other players who enter its radius are struck with
 * blindness and wither. The horcrux item is protected from pickup and despawning.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Horcrux-making_spell">Harry Potter Wiki - Horcrux-making spell</a>
 */
public class HORCRUX extends O2StationarySpell {
    /**
     * The death message shown to the caster when the horcrux protects them.
     */
    public static final String deathMessage = "Death approaches but cannot claim your soul.";

    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 3;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 3;

    /**
     * Duration bound, unused because Horcrux is permanent.
     */
    public static final int minDurationConfig = 1000;

    /**
     * Duration bound, unused because Horcrux is permanent.
     */
    public static final int maxDurationConfig = 1000;

    /**
     * The material of the horcrux item.
     */
    private Material horcruxMaterial;

    /**
     * The item entity in the world that is this horcrux.
     */
    private Item horcruxItem;

    /**
     * The name of the world this horcrux is in.
     */
    private String worldName;

    /**
     * Players currently under this horcrux's effects, mapped to their remaining effect ticks; prevents reapplying the
     * effects every move.
     */
    private final Map<UUID, Integer> affectedPlayers = new HashMap<>();

    /**
     * How long, in ticks, the blindness and wither effects last on a player who enters the area.
     */
    public static final int effectDuration = Ollivanders2Common.ticksPerSecond * 15;

    /**
     * Serialization key for {@link #horcruxMaterial}.
     */
    private final String materialLabel = "itemType";

    /**
     * Serialization key for {@link #worldName}.
     */
    private final String worldLabel = "world";

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
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
     * Constructor for casting a new Horcrux spell anchored to the given item.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the horcrux anchor
     * @param item     the item representing the horcrux in the world
     */
    public HORCRUX(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull Item item) {
        super(plugin);

        spellType = O2StationarySpellType.HORCRUX;
        permanent = true;

        setPlayerID(pid);
        setLocation(location);
        radius = minRadius = maxRadius = minRadiusConfig;
        duration = 10;

        worldName = world.getName();
        horcruxItem = item;
        horcruxMaterial = item.getItemStack().getType();

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
     * Count down the remaining effect time for each affected player, dropping those whose effects have expired.
     */
    @Override
    public void upkeep() {
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
     * @return the serialized spell data, holding the horcrux material and world name
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
     * Restore the horcrux material and world name from saved data. Kills the spell if the material no longer exists
     * (e.g. removed in a Minecraft update) or if either value is missing.
     *
     * @param spellData the serialized spell data
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
     * Respawn the horcrux item when its caster joins, if it is missing. This runs on join rather than world load
     * because world load fires before the plugin's listeners are registered.
     *
     * @param event the player join event
     */
    @Override
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        if (!event.getPlayer().getUniqueId().equals(playerUUID))
            return;

        Item horcrux = EntityCommon.getItemAtLocation(location);

        // no item here, or a different item is here
        if (horcrux == null || (horcrux.getItemStack().getType() != horcruxMaterial)) {
            respawnHorcruxItem();
        }
    }

    /**
     * Drop a fresh horcrux item of the saved material at the spell location. Kills the spell if its world is unloaded.
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
     * On the caster's death, set their respawn point to this horcrux, keep their inventory, level, and experience, and
     * show a custom death message. Other players' deaths are unaffected.
     *
     * @param event the player death event
     */
    @Override
    void doOnPlayerDeathEvent(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getUniqueId().equals(playerUUID)) {
            player.setRespawnLocation(location, true);

            event.setDeathMessage(deathMessage);
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.setNewTotalExp(player.getTotalExperience());
        }
    }

    /**
     * Apply blindness (amplifier 2) and wither (amplifier 1) to a non-caster player who enters the area, unless they
     * are already affected. The caster is immune.
     *
     * @param event the player move event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLocation = event.getTo();
        if (toLocation == null || !isLocationInside(toLocation))
            return;

        Player target = event.getPlayer();

        if (target.getUniqueId().equals(playerUUID)) {
            common.printDebugMessage("HORCRUX: " + target.getName() + " enter their own horcrux area", null, null, false);
            return;
        }

        if (affectedPlayers.containsKey(target.getUniqueId()))
            return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, effectDuration, 1));

        affectedPlayers.put(target.getUniqueId(), effectDuration);
    }

    /**
     * Stop the horcrux item from despawning.
     *
     * @param event the item despawn event
     */
    @Override
    void doOnItemDespawnEvent(@NotNull ItemDespawnEvent event) {
        if (horcruxItem == null) // can be null on restart before the item is loaded
            return;

        Item eventItem = event.getEntity();

        if (eventItem.getUniqueId().equals(horcruxItem.getUniqueId()))
            event.setCancelled(true);
    }

    /**
     * Stop an entity picking up the horcrux item.
     *
     * @param event the entity pickup item event
     */
    @Override
    void doOnEntityPickupItemEvent(@NotNull EntityPickupItemEvent event) {
        if (horcruxItem == null) // can be null on restart before the item is loaded
            return;

        Item eventItem = event.getItem();

        if (eventItem.getUniqueId().equals(horcruxItem.getUniqueId()))
            event.setCancelled(true);
    }

    /**
     * Stop a hopper or other inventory collector picking up the horcrux item.
     *
     * @param event the inventory pickup item event
     */
    @Override
    void doOnInventoryItemPickupEvent(@NotNull InventoryPickupItemEvent event) {
        if (horcruxItem == null) // can be null on restart before the item is loaded
            return;

        Item eventItem = event.getItem();

        if (eventItem.getUniqueId().equals(horcruxItem.getUniqueId()))
            event.setCancelled(true);
    }

    @Override
    void doCleanUp() {
    }

    /**
     * @return true if the caster UUID, location, and horcrux material are all present
     */
    @Override
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null && horcruxMaterial != null;
    }

    /**
     * Whether a player is currently under this horcrux's effects.
     *
     * @param player the player to check
     * @return true if they are affected, false otherwise
     */
    public boolean isAffected(Player player) {
        return affectedPlayers.containsKey(player.getUniqueId());
    }
}
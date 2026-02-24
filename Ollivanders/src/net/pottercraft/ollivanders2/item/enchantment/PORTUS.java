package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PORTUS portkey enchantment for items.
 * <p>
 * PORTUS is an expert-level enchantment that transforms an item into a portkey—a magical transportation
 * device that teleports players to a predetermined destination when picked up. Portkeys are powerful
 * magical objects that can transport multiple players at once within a configurable radius.</p>
 * <p>
 * Portkey behavior:</p>
 * <ul>
 * <li>Player pickup: teleports the player and all players within the radius to the destination</li>
 * <li>Non-player pickup: blocked, only players can activate portkeys</li>
 * <li>Hopper pickup: blocked, portkeys cannot be collected by automated systems</li>
 * <li>Round-trip: after activation, the destination is updated to return to the pickup location</li>
 * <li>Proximity check: portkeys won't activate if the player is within 3 blocks of the destination</li>
 * </ul>
 * <p>
 * Teleportation can be blocked by stationary spells:</p>
 * <ul>
 * <li>{@link O2StationarySpellType#NULLUM_EVANESCUNT}: blocks teleportation FROM a location</li>
 * <li>{@link O2StationarySpellType#NULLUM_APPAREBIT}: blocks teleportation TO a location</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.spell.PORTUS the spell that creates portkeys
 * @see <a href="https://harrypotter.fandom.com/wiki/Portkey">https://harrypotter.fandom.com/wiki/Portkey</a>
 */
public class PORTUS extends Enchantment {
    /**
     * The teleport destination for this portkey
     */
    Location destination;

    /**
     * The radius that a portkey will affect
     */
    int radius = 2;

    /**
     * Portkey radius config label
     */
    String portkeyRadiusConfigLabel = "portkeyRadius";

    /**
     * Constructor for creating a PORTUS portkey enchantment instance.
     * <p>
     * Parses the destination location from the args parameter and optionally reads the portkey radius
     * from the plugin configuration (key: "portkeyRadius"). If the destination cannot be parsed
     * (invalid format, world not found), the portkey will not function.
     * </p>
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment
     * @param args     the destination location in the format "world_name X Y Z", e.g., "world 10 100 5"
     * @param itemLore optional custom lore to display on the enchanted portkey item
     */
    public PORTUS(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.PORTUS;

        parseLocation();
        if (p.getConfig().isSet(portkeyRadiusConfigLabel))
           radius = p.getConfig().getInt(portkeyRadiusConfigLabel);
    }

    /**
     * Parse the location from the args in the format "world_name X Y Z", ex. "world 10 100 5"
     */
    private void parseLocation() {
        if (args == null)
            return;

        // X, Y, Z should be in the args
        String[] split = args.split(" ");

        double x;
        double y;
        double z;

        if (split.length != 4) {
            common.printDebugMessage("Invalid coordinates on Portkey", null, null, false);
            return;
        }

        try {
            x = Double.parseDouble(split[1]);
            y = Double.parseDouble(split[2]);
            z = Double.parseDouble(split[3]);
        }
        catch (Exception e) {
            common.printDebugMessage("Failed to parse coordinates on Portkey", null, null, false);
            return;
        }

        World world = p.getServer().getWorld(split[0]);
        if (world == null)
            return;

        destination = new Location(world, x, y, z);
    }

    /**
     * Activate the portkey when a player picks it up.
     * <p>
     * When a player picks up a portkey, this method teleports the player and all nearby players
     * (within the configured radius) to the portkey's destination. After teleportation, the
     * destination is updated to the pickup location, creating a round-trip portkey.</p>
     * <p>
     * Teleportation is blocked when:</p>
     * <ul>
     * <li>The entity is not a player (event is cancelled)</li>
     * <li>A {@link O2StationarySpellType#NULLUM_EVANESCUNT} spell blocks departure from the location</li>
     * <li>A {@link O2StationarySpellType#NULLUM_APPAREBIT} spell blocks arrival at the destination</li>
     * <li>The player is within 3 blocks of the destination (prevents accidental self-teleportation)</li>
     * <li>The destination is null (portkey was not properly configured)</li>
     * </ul>
     *
     * @param event the entity item pickup event
     */
    @Override
    public void doEntityPickupItem(@NotNull EntityPickupItemEvent event) {
        Entity entity = event.getEntity();

        // prevent non-player entities picking this up
        if (!(entity instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        // portkey must have a valid destination
        if (destination == null) {
            common.printDebugMessage("Portkey has null destination", null, null, true);
            return;
        }

        Location location = entity.getLocation();

        // can players teleport from this location?
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location)) {
            if (stationarySpell.getSpellType() == O2StationarySpellType.NULLUM_EVANESCUNT)
                return;
        }

        // can players teleport to the destination?
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(destination)) {
            if (stationarySpell.getSpellType() == O2StationarySpellType.NULLUM_APPAREBIT)
                return;
        }

        // if this location is within 3 blocks of player already is, don't activate the port key. This will prevent it trying
        // to teleport the player that just created it and is trying to pick it up
        if (location.distance(destination) <= 3)
            return;

        // set the return location for the portkey
        Location portkeyReturn = event.getItem().getLocation();

        // teleport the player and any other player in the radius
        p.addTeleportAction((Player)entity, destination, true);
        for (LivingEntity livingEntity : EntityCommon.getLivingEntitiesInRadius(location, radius)) {
            if (livingEntity instanceof Player) {
                p.addTeleportAction((Player)livingEntity, destination, true);
            }
            else {
                livingEntity.teleport(destination);
            }
        }

        // update the portkey's destination to return to its starting location
        destination = portkeyReturn;
    }

    /**
     * Prevent hoppers and block inventories from picking up portkeys.
     * <p>
     * Portkeys can only be activated by players, so automated collection systems are blocked.
     * This ensures portkeys remain available for players to find and use as intended.
     * </p>
     *
     * @param event the inventory pickup item event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * No special handling for item drop events.
     * <p>
     * PORTUS portkeys do not have any special behavior when dropped. The portkey only
     * activates when picked up by a player, not when dropped.
     * </p>
     *
     * @param event the player drop item event (not used)
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
    }

    /**
     * No special handling for item held events.
     * <p>
     * PORTUS portkeys do not have any special behavior when the player switches between hotbar slots.
     * The portkey only activates when picked up from the world, not when held or switched to.
     * </p>
     *
     * @param event the player item held event (not used)
     */
    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
    }

    /**
     * Get the effect radius for this portkey
     *
     * @return the effect radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Get the destination location for this portkey.
     * <p>
     * Returns a clone of the destination so that it cannot be modified by other classes.
     * The destination may be null if the portkey was not properly configured (invalid args format,
     * world not found, etc.).
     * </p>
     *
     * @return the destination location, or null if the portkey destination was not properly configured
     */
    @Nullable
    public Location getDestination() {
        if (destination == null)
            return null;

        return destination.clone();
    }
}

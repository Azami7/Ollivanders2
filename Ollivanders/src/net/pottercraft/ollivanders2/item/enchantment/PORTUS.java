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
 * Portkey enchantment: turns an item into a portkey that teleports the picking-up player, and any players within its
 * radius, to a destination, then flips the destination to the pickup location for a round trip. Blocked by
 * {@link O2StationarySpellType#NULLUM_EVANESCUNT} at the origin and {@link O2StationarySpellType#NULLUM_APPAREBIT} at
 * the destination.
 *
 * @see net.pottercraft.ollivanders2.spell.PORTUS
 * @see <a href="https://harrypotter.fandom.com/wiki/Portkey">Harry Potter Wiki - Portkey</a>
 */
public class PORTUS extends Enchantment {
    /**
     * The teleport destination for this portkey.
     */
    Location destination;

    /**
     * The radius within which other players are also teleported.
     */
    int radius = 2;

    /**
     * Config key for the portkey radius.
     */
    String portkeyRadiusConfigLabel = "portkeyRadius";

    /**
     * Constructor. Parses the destination from {@code args} and overrides the radius from config if set; an
     * unparseable destination leaves the portkey inert.
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment
     * @param args     the destination location as "world_name X Y Z", e.g. "world 10 100 5"
     * @param itemLore optional custom lore for the enchanted portkey item
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
     * Activate the portkey: teleport the picking-up player and any players within the radius to the destination, then
     * reset the destination to the pickup location for the return trip. Cancels the event for non-player entities.
     * Does nothing if the destination is null, if a blocking stationary spell is present at the origin
     * ({@link O2StationarySpellType#NULLUM_EVANESCUNT}) or destination ({@link O2StationarySpellType#NULLUM_APPAREBIT}),
     * or if the player is already within 3 blocks of the destination.
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
     * Prevent block inventories (e.g. hoppers) from collecting the portkey.
     *
     * @param event the inventory pickup item event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
    }

    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
    }

    /**
     * Get the radius within which other players are also teleported.
     *
     * @return the effect radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Get a defensive copy of this portkey's destination.
     *
     * @return a clone of the destination, or null if the portkey was not configured with a valid one
     */
    @Nullable
    public Location getDestination() {
        if (destination == null)
            return null;

        return destination.clone();
    }
}

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
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Portkey enchantment. A portkey teleports a player
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.PORTUS}
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Portkey">https://harrypotter.fandom.com/wiki/Portkey</a>
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
     * Constructor
     *
     * @param plugin   a callback to the plugin
     * @param mag      the magnitude of this enchantment
     * @param args     optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
     */
    public PORTUS(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.PORTUS;

        parseLocation();
        if (p.getConfig().isSet(portkeyRadiusConfigLabel))
           radius = p.getConfig().getInt(portkeyRadiusConfigLabel);
    }

    /**
     * Parse the location from the args
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
     * Don't let portkeys despawn
     *
     * @param event the item despawn event
     */
    @Override
    public void doItemDespawn(@NotNull ItemDespawnEvent event) {
        event.setCancelled(true);
    }

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    @Override
    public void doEntityPickupItem(@NotNull EntityPickupItemEvent event) {
        Entity entity = event.getEntity();

        // prevent non-player entities picking this up
        if (!(entity instanceof Player)) {
            event.setCancelled(true);
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
        p.addTeleportEvent((Player)entity, destination, true);
        for (LivingEntity livingEntity : EntityCommon.getLivingEntitiesInRadius(location, radius)) {
            if (livingEntity instanceof Player) {
                p.addTeleportEvent((Player)livingEntity, destination, true);
            }
            else {
                livingEntity.teleport(destination);
            }
        }

        // update the portkey's destination to return to its starting location
        destination = portkeyReturn;
    }

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * Handle item drop events
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
    }

    /**
     * Handle item held events
     *
     * @param event the item drop event
     */
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
    }
}

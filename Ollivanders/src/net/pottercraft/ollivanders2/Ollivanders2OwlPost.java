package net.pottercraft.ollivanders2;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * In newer versions of MC triggering teleport events from AsyncChatEvents is no longer thread-safe. Need to create a queue of owl post events like we use for
 * things like spell projectiles and effects.
 */
public class Ollivanders2OwlPost
{
    /**
     * The words the player has to say to trigger owl post
     */
    public static final String deliveryKeyword = "deliver to";

    /**
     * The queue of pending deliveries
     */
    private final ArrayList<Delivery> deliveryQueue = new ArrayList<>();

    /**
     * The delivery entity type
     */
    public static final EntityType owlPostEntityType = EntityType.PARROT;

    /**
     * Reference to the plugin
     */
    Ollivanders2 p;

    /**
     * Represents a delivery
     */
    static private class Delivery
    {
        UUID sender;
        String senderName;
        UUID recipient;
        Entity courier;
        ItemStack deliveryPackage;
        Location courierOriginalLocation;

        /**
         * Wait before delivering item - owls have to get ready fly
         */
        int cooldown = Ollivanders2Common.ticksPerSecond;

        /**
         * When a recipient is offline or a place the owl cannot go, queue for retry
         */
        public static final int retryCooldown = 5 * Ollivanders2Common.ticksPerMinute;

        /**
         * Constructor.
         *
         * @param from   UUID of the player sending the delivery
         * @param to     UUID of the player receiving the delivery
         * @param entity the delivery
         * @param item   the item to deliver
         */
        Delivery(@NotNull Player from, @NotNull UUID to, @NotNull Entity entity, @NotNull ItemStack item)
        {
            sender = from.getUniqueId();
            senderName = from.getName();
            recipient = to;
            courier = entity;
            deliveryPackage = item;
            courierOriginalLocation = entity.getLocation();
        }
    }

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public Ollivanders2OwlPost(@NotNull Ollivanders2 plugin)
    {
        p = plugin;
    }

    /**
     * Add a delivery to the delivery queue
     *
     * @param from   UUID of the player sending the delivery
     * @param to     UUID of the player receiving the delivery
     * @param entity the delivery
     * @param item   the item to deliver
     */
    public void addDelivery(@NotNull Player from, @NotNull UUID to, @NotNull Entity entity, @NotNull ItemStack item)
    {
        Delivery delivery = new Delivery(from, to, entity, item);

        deliveryQueue.add(delivery);
    }

    /**
     * Process an owl post request by a player.
     *
     * @param player  the player requesting delivery
     * @param message the delivery request message
     */
    public void processOwlPostRequest(@NotNull Player player, @NotNull String message)
    {
        if (!message.toLowerCase().startsWith(Ollivanders2OwlPost.deliveryKeyword.toLowerCase()))
            return;

        // who do they want to deliver to?
        String[] splitString = message.split(" ");
        if (splitString.length != 3)
        {
            Ollivanders2API.common.printDebugMessage("Ollivanders2OwlPost.processOwlPostRequest: bad request \"" + message + "\"", null, null, false);
            return;
        }

        // find recipient
        O2Player recipient = Ollivanders2API.getPlayers().getPlayer(splitString[2]);
        if (recipient == null)
        {
            player.sendMessage(Ollivanders2.chatColor + "Player " + splitString[2] + " not found.");
            return;
        }

        // are they standing close an entity that can deliver?
        Location location = player.getLocation();
        List<Entity> nearbyEntities = EntityCommon.getNearbyEntitiesByType(player.getLocation(), 5, owlPostEntityType);
        if (nearbyEntities.size() < 1)
        {
            player.sendMessage(Ollivanders2.chatColor + "Player " + splitString[2] + " not found.");
            return;
        }

        // is the player holding an item?
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR)
        {
            player.sendMessage(Ollivanders2.chatColor + "No item in your primary hand. Please hold the item you wish to send.");
            return;
        }

        addDelivery(player, recipient.getID(), nearbyEntities.get(0), held);
        Ollivanders2API.common.printDebugMessage("Added owl post delivery from " + player.getName() + " to " + recipient.getPlayerName(), null, null, false);

        player.getInventory().setItemInMainHand(null);
    }

    /**
     * Run the game tick upkeep for owl post deliveries
     */
    public void upkeep()
    {
        ArrayList<Delivery> deliveryQueueCopy = new ArrayList<>(deliveryQueue);

        for (Delivery delivery : deliveryQueueCopy)
        {
            if (delivery.cooldown > 0)
            {
                delivery.cooldown = delivery.cooldown - 1;
                continue;
            }

            if (doDelivery(delivery))
            {
                deliveryQueue.remove(delivery);
            }
        }
    }

    /**
     * Do an owl post delivery
     *
     * @param delivery the delivery to deliver
     */
    private boolean doDelivery(Delivery delivery)
    {
        // is the recipient online?
        Player player = p.getServer().getPlayer(delivery.recipient);
        if (player == null)
        {
            // player is not online, reset cooldown so we can wait for them
            delivery.cooldown = Delivery.retryCooldown;

            Ollivanders2API.common.printDebugMessage("Owl post recipient " + delivery.recipient + " offline, deferring delivery", null, null, false);
            return false;
        }

        Location playerLocation = player.getLocation();
        Location deliveryLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY() + 2, playerLocation.getZ());
        if (deliveryLocation.getWorld() == null)
        {
            Ollivanders2API.common.printDebugMessage("Ollivanders2OwlPost.doDelivery: delivery location world is null", null, null, true);
            return false;
        }

        Material blockType = deliveryLocation.getWorld().getBlockAt(deliveryLocation).getType();

        // can the owl get to this delivery location?
        if (!(blockType == Material.AIR || blockType == Material.CAVE_AIR))
        {
            // owl cannot go to this place
            delivery.cooldown = Delivery.retryCooldown;

            Ollivanders2API.common.printDebugMessage("Owl post recipient " + delivery.recipient + " is in a place the owl cannot go, deferring delivery", null, null, false);
            return false;
        }

        delivery.courierOriginalLocation = delivery.courier.getLocation();
        delivery.courier.teleport(deliveryLocation);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                deliverItemToPlayer(delivery.senderName, player, delivery.deliveryPackage, delivery.courier, delivery.courierOriginalLocation);
            }
        }.runTaskLater(p, 2 * Ollivanders2Common.ticksPerSecond);

        return true;
    }

    /**
     * Do the delivery to a player
     *
     * @param senderName     the name of the player who sent the delivery
     * @param recipient      the recipient
     * @param item           the item to deliver
     * @param courier        the courier of the delivery
     * @param returnLocation the courier's original location
     */
    private void deliverItemToPlayer(String senderName, Player recipient, ItemStack item, Entity courier, Location returnLocation)
    {
        recipient.sendMessage(Ollivanders2.chatColor + "An owl post delivery arrives for you from " + senderName + ".");
        List<ItemStack> kit = new ArrayList<>();
        kit.add(item);
        O2PlayerCommon.givePlayerKit(recipient, kit);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                courier.teleport(returnLocation);
            }
        }.runTaskLater(p, 5 * Ollivanders2Common.ticksPerSecond);
    }
}

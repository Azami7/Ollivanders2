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
 * The owl post system, which lets a player send a held item to another player by speaking to a nearby owl.
 * <p>
 * Requests arrive from chat, which is handled asynchronously, but teleporting the courier is only safe on the main
 * thread. Deliveries are therefore queued here and carried out by {@link #upkeep()} on the plugin's game tick
 * scheduler, the same pattern used for spell projectiles and effects.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Owl_Post">Harry Potter Wiki - Owl Post</a>
 */
public class Ollivanders2OwlPost {
    /**
     * The words the player has to say to trigger owl post, followed by the recipient's name
     */
    public static final String deliveryKeyword = "deliver to";

    /**
     * The deliveries waiting to be made, in the order they were requested
     */
    private final ArrayList<Delivery> deliveryQueue = new ArrayList<>();

    /**
     * The entity type that can act as a courier
     */
    public static final EntityType owlPostEntityType = EntityType.PARROT;

    /**
     * Reference to the plugin
     */
    Ollivanders2 p;

    /**
     * A single package awaiting delivery, along with the courier carrying it.
     */
    static private class Delivery {
        /**
         * The player who sent the package
         */
        UUID sender;

        /**
         * The sender's name when the delivery was requested, so messages about it do not require them to be online
         */
        String senderName;

        /**
         * The player the package is for
         */
        UUID recipient;

        /**
         * The owl carrying the package
         */
        Entity courier;

        /**
         * The item being delivered
         */
        ItemStack deliveryPackage;

        /**
         * Where the courier was before it flew off, so it can be sent home once the delivery is made
         */
        Location courierOriginalLocation;

        /**
         * Ticks remaining before the next delivery attempt. The initial value gives the owl time to get ready to fly.
         */
        int cooldown = Ollivanders2Common.ticksPerSecond;

        /**
         * How long to wait before retrying when the recipient is offline or somewhere the owl cannot go
         */
        public static final int retryCooldown = 5 * Ollivanders2Common.ticksPerMinute;

        /**
         * Constructor.
         *
         * @param from   the player sending the delivery
         * @param to     the player receiving the delivery
         * @param entity the courier that will carry the delivery
         * @param item   the item to deliver
         */
        Delivery(@NotNull Player from, @NotNull UUID to, @NotNull Entity entity, @NotNull ItemStack item) {
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
    public Ollivanders2OwlPost(@NotNull Ollivanders2 plugin) {
        p = plugin;
    }

    /**
     * Queue a delivery. The item is delivered no sooner than the next second of game time and, if the recipient is
     * offline or unreachable, the delivery stays queued and is retried until it succeeds or the plugin disables.
     *
     * @param from   the player sending the delivery
     * @param to     the player receiving the delivery
     * @param entity the courier that will carry the delivery
     * @param item   the item to deliver; the caller is responsible for removing it from the sender's inventory
     */
    public void addDelivery(@NotNull Player from, @NotNull UUID to, @NotNull Entity entity, @NotNull ItemStack item) {
        Delivery delivery = new Delivery(from, to, entity, item);

        deliveryQueue.add(delivery);
    }

    /**
     * Get the number of deliveries still waiting to be made.
     *
     * @return the number of pending deliveries
     */
    public int getPendingDeliveryCount() {
        return deliveryQueue.size();
    }

    /**
     * Queue an owl post delivery requested by a player in chat, taking the item from their primary hand.
     *
     * <p>The request must be {@link #deliveryKeyword} followed by the recipient's name, and the sender must be within
     * five blocks of an owl and holding the item to send. Requests that do not have that form are ignored, and the
     * player is told why any other failure occurred.</p>
     *
     * @param player  the player requesting delivery
     * @param message the chat message requesting the delivery
     */
    public void processOwlPostRequest(@NotNull Player player, @NotNull String message) {
        if (!message.toLowerCase().startsWith(Ollivanders2OwlPost.deliveryKeyword.toLowerCase()))
            return;

        // a well-formed request is the two keyword words followed by the recipient name, so exactly three words
        String[] splitString = message.split(" ");
        if (splitString.length != 3) {
            Ollivanders2API.common.printDebugMessage("Ollivanders2OwlPost.processOwlPostRequest: bad request \"" + message + "\"", null, null, false);
            return;
        }

        O2Player recipient = Ollivanders2API.getPlayers().getPlayer(splitString[2]);
        if (recipient == null) {
            player.sendMessage(Ollivanders2.chatColor + "Player " + splitString[2] + " not found.");
            return;
        }

        // the sender has to be near an owl to hand their package to
        List<Entity> nearbyEntities = EntityCommon.getNearbyEntitiesByType(player.getLocation(), 5, owlPostEntityType);
        if (nearbyEntities.size() < 1) {
            player.sendMessage(Ollivanders2.chatColor + "No owl was found nearby.");
            return;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR) {
            player.sendMessage(Ollivanders2.chatColor + "No item in your primary hand. Please hold the item you wish to send.");
            return;
        }

        // clone the item so the queued delivery is not affected by clearing the player's hand
        addDelivery(player, recipient.getID(), nearbyEntities.get(0), held.clone());
        Ollivanders2API.common.printDebugMessage("Added owl post delivery from " + player.getName() + " to " + recipient.getPlayerName(), null, null, false);

        player.getInventory().setItemInMainHand(null);
        player.sendMessage(Ollivanders2.chatColor + "Owl post delivery for " + recipient.getPlayerName() + " scheduled.");
    }

    /**
     * Run the game tick upkeep for owl post deliveries. Counts down each pending delivery's cooldown and attempts the
     * ones that are ready, removing them from the queue once their courier is on its way.
     *
     * <p>Must be called once per game tick from the plugin scheduler; cooldown durations assume that rate.</p>
     */
    public void upkeep() {
        ArrayList<Delivery> deliveryQueueCopy = new ArrayList<>(deliveryQueue);

        for (Delivery delivery : deliveryQueueCopy) {
            if (delivery.cooldown > 0) {
                delivery.cooldown = delivery.cooldown - 1;
                continue;
            }

            if (doDelivery(delivery)) {
                deliveryQueue.remove(delivery);
            }
        }
    }

    /**
     * Attempt an owl post delivery, sending the courier to the recipient and scheduling the hand-off shortly after.
     *
     * <p>If the recipient is offline or standing somewhere the owl cannot reach, the delivery is deferred by
     * {@link Delivery#retryCooldown} ticks instead.</p>
     *
     * @param delivery the delivery to attempt
     * @return true if the delivery should be dropped from the queue, either because it is underway or because it can
     * never be made, false if it should be retried later
     */
    private boolean doDelivery(@NotNull Delivery delivery) {
        Player player = p.getServer().getPlayer(delivery.recipient);
        if (player == null) {
            // the recipient is offline, wait and try again in case they come back
            delivery.cooldown = Delivery.retryCooldown;

            Ollivanders2API.common.printDebugMessage("Owl post recipient " + delivery.recipient + " offline, deferring delivery", null, null, false);
            return false;
        }

        Location playerLocation = player.getLocation();
        Location deliveryLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY() + 2, playerLocation.getZ());

        if (deliveryLocation.getWorld() == null) {
            Ollivanders2API.common.printDebugMessage("Ollivanders2OwlPost.doDelivery: delivery location world is null", null, null, true);
            return true; // a lie, but this will get upkeep to remove this delivery so it doesn't retry forever
        }

        Material blockType = deliveryLocation.getWorld().getBlockAt(deliveryLocation).getType();

        // the owl needs open air above the recipient to fly in to, so wait for them to move somewhere it can reach
        if (!(blockType == Material.AIR || blockType == Material.CAVE_AIR)) {
            delivery.cooldown = Delivery.retryCooldown;

            Ollivanders2API.common.printDebugMessage("Owl post recipient " + delivery.recipient + " is in a place the owl cannot go, deferring delivery", null, null, false);
            return false;
        }

        // the owl may have wandered since the request, so home is wherever it is now
        delivery.courierOriginalLocation = delivery.courier.getLocation();
        delivery.courier.teleport(deliveryLocation);

        new BukkitRunnable() {
            @Override
            public void run() {
                deliverItemToPlayer(delivery.senderName, player, delivery.deliveryPackage, delivery.courier, delivery.courierOriginalLocation);
            }
        }.runTaskLater(p, 2 * Ollivanders2Common.ticksPerSecond);

        return true;
    }

    /**
     * Hand a package to its recipient and send the courier home a few seconds later. Anything that does not fit in the
     * recipient's inventory is dropped at their feet.
     *
     * @param senderName     the name of the player who sent the delivery
     * @param recipient      the player receiving the delivery
     * @param item           the item to deliver
     * @param courier        the courier that carried the delivery
     * @param returnLocation where to send the courier once the delivery is made
     */
    private void deliverItemToPlayer(@NotNull String senderName, @NotNull Player recipient, @NotNull ItemStack item, @NotNull Entity courier, @NotNull Location returnLocation) {
        recipient.sendMessage(Ollivanders2.chatColor + "An owl post delivery arrives for you from " + senderName + ".");
        List<ItemStack> kit = new ArrayList<>();
        kit.add(item);
        O2PlayerCommon.givePlayerKit(recipient, kit);

        new BukkitRunnable() {
            @Override
            public void run() {
                courier.teleport(returnLocation);
            }
        }.runTaskLater(p, 5 * Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Return an undelivered package to the player who sent it, putting it in their inventory if they are online and
     * dropping it at the courier's location if they are not, so that the item is never destroyed.
     *
     * @param delivery the undelivered delivery
     */
    private void returnDeliveryToSender(@NotNull Delivery delivery) {
        Player sender = p.getServer().getPlayer(delivery.sender);

        if (sender != null) {
            List<ItemStack> kit = new ArrayList<>();
            kit.add(delivery.deliveryPackage);
            O2PlayerCommon.givePlayerKit(sender, kit);

            sender.sendMessage(Ollivanders2.chatColor + "Your owl post delivery could not be made and has been returned to you.");
            return;
        }

        // on a server shutdown players are kicked before plugins disable, so this is the usual path
        Location returnLocation = delivery.courierOriginalLocation;
        if (returnLocation.getWorld() == null) {
            Ollivanders2API.common.printDebugMessage("Ollivanders2OwlPost.returnDeliveryToSender: unable to return package to " + delivery.senderName, null, null, true);
            return;
        }

        returnLocation.getWorld().dropItem(returnLocation, delivery.deliveryPackage);
        Ollivanders2API.common.printDebugMessage("Dropped undelivered owl post package for offline sender " + delivery.senderName, null, null, false);
    }

    /**
     * Cleanup when the plugin disables. Pending deliveries do not survive a restart, so every package still in the
     * queue is returned to its sender and the queue is emptied.
     *
     * @see #returnDeliveryToSender(Delivery)
     */
    public void onDisable() {
        // TODO: persist the delivery queue across restarts instead of returning packages - task 506
        for (Delivery delivery : deliveryQueue) {
            returnDeliveryToSender(delivery);
        }

        deliveryQueue.clear();
    }
}

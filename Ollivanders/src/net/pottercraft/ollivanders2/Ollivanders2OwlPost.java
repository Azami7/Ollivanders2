package net.pottercraft.ollivanders2;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * In newer versions of MC triggering teleport events from AsyncChatEvents is no longer thread-safe. Need to create a queue of owl post events like we use for
 * things like spell projectiles and effects.
 *
 * @author Azami7
 */
public class Ollivanders2OwlPost
{
    private final ArrayList<Delivery> deliveryQueue = new ArrayList<>();

    EntityType owlPostEntityType = EntityType.PARROT;

    private static final List<EntityType> owlPostAllowedEntities = new ArrayList<>()
    {{
        add(EntityType.PARROT);
        add(EntityType.CAT);
        add(EntityType.WOLF);
        add(EntityType.HORSE);
        add(EntityType.MULE);
        add(EntityType.DONKEY);
        add(EntityType.LLAMA);
        add(EntityType.SKELETON_HORSE);
    }};

    private class Delivery
    {
        UUID sender;
        UUID recipient;
        Entity courier;
        ItemStack deliveryPackage;
        Location courierOriginalLocation;

        Delivery (UUID from, UUID to, Entity entity, ItemStack item, Location startingLocation)
        {
            sender = from;
            recipient = to;
            courier = entity;
            deliveryPackage = item;
            courierOriginalLocation = startingLocation;
        }
    }
}

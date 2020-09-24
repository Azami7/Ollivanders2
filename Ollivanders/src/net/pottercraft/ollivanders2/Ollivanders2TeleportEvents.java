package net.pottercraft.ollivanders2;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * With MV 1.14, triggering PlayerTeleportEvents from other events is no longer thread-safe. Need to create a queue of teleport events like we use for
 * things like spell projectiles and effects.
 *
 * @author Azami7
 * @since 2.4
 */
public class Ollivanders2TeleportEvents
{
   private Ollivanders2 p;

   /**
    * The list of all queued teleport events
    */
   private ArrayList<O2TeleportEvent> teleportEvents = new ArrayList<>();

   /**
    * A teleport event
    */
   public class O2TeleportEvent
   {
      /**
       * The player to teleport
       */
      private Player player;

      /**
       * The location they are teleporting from
       */
      private Location fromLocation;

      /**
       * The location they are teleporting to
       */
      private Location toLocation;

      /**
       * Create explosion effect on teleport
       */
      private boolean explosionOnTeleport = false;

      /**
       * Constructor
       *
       * @param p    the player teleporting
       * @param from the location they are teleporting from
       * @param to   the location they are teleporting to
       */
      O2TeleportEvent(Player p, Location from, Location to)
      {
         player = p;
         fromLocation = from;
         toLocation = to;
      }

      /**
       * Constructor
       *
       * @param from      the location they are teleporting from
       * @param to        the location they are teleporting to
       * @param explosion should this teleport create an explosion effect when it happens
       */
      O2TeleportEvent(Player p, Location from, Location to, boolean explosion)
      {
         player = p;
         fromLocation = from;
         toLocation = to;

         explosionOnTeleport = explosion;
      }

      public Player getPlayer()
      {
         return player;
      }

      public Location getToLocation()
      {
         return toLocation;
      }

      public Location getFromLocation()
      {
         return fromLocation;
      }

      public boolean isExplosionOnTeleport()
      {
         return explosionOnTeleport;
      }
   }

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    */
   public Ollivanders2TeleportEvents(Ollivanders2 plugin)
   {
      p = plugin;
   }

   /**
    * Get all of the teleport events.
    *
    * @return an array of the pending teleport events
    */
   @NotNull
   public O2TeleportEvent[] getTeleportEvents()
   {
      return teleportEvents.toArray(new O2TeleportEvent[teleportEvents.size()]);
   }

   /**
    * Add a teleport event to the list.
    *
    * @param player the player teleporting
    * @param from   the location they are teleporting from
    * @param to     the location they are teleporting to
    */
   public void addTeleportEvent(Player player, Location from, Location to)
   {
      addTeleportEvent(player, from, to, false);
   }

   /**
    * Add a teleport event to the list.
    *
    * @param player              the player teleporting
    * @param from                the location they are teleporting from
    * @param to                  the location they are teleporting to
    * @param explosionOnTeleport should there be an explosion effect on teleport
    */
   public void addTeleportEvent(Player player, Location from, Location to, boolean explosionOnTeleport)
   {
      O2TeleportEvent teleportEvent = new O2TeleportEvent(player, from, to, explosionOnTeleport);

      if (Ollivanders2.debug)
         p.getLogger().info("Created teleport event: " + player.getName() + " from " + from.toString() + " to " + to.toString());

      teleportEvents.add(teleportEvent);
   }

   /**
    * Remove a teleport event from the list.
    *
    * @param event the teleport event to remove
    */
   public void removeTeleportEvent(O2TeleportEvent event)
   {
      if (teleportEvents.contains(event))
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Removing teleport event for " + event.getPlayer().getName());
         teleportEvents.remove(event);
      }
      else
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("Unable to remove teleport event, not found.");
         }
      }
   }
}

package net.pottercraft.ollivanders2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON;
import org.jetbrains.annotations.NotNull;

/**
 * Scheduler for Ollivanders2
 *
 * @author Azami7
 * @author lownes
 */
public class OllivandersSchedule implements Runnable
{
   /**
    * A callback to the plugin
    */
   final private Ollivanders2 p;

   /**
    * Counts game ticks
    */
   private int scheduleTimer = 0;

   /**
    * Reset the counter after this many ticks to prevent it growing unbounded
    */
   private final static int timerReset = 86400 * Ollivanders2Common.ticksPerSecond;

   /**
    * A list of players flying on brooms
    */
   final private static Set<UUID> flying = new HashSet<>();

   /**
    * A list of players on brooms
    */
   final private Set<UUID> onBroom = new HashSet<>();

   /**
    * Common functions
    */
   final private Ollivanders2Common common;

   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   OllivandersSchedule (@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(p);
   }

   /**
    * Primary plugin thread
    */
   public void run ()
   {
      // run every tick
      try
      {
         projectileSched();
         oeffectSched();
         Ollivanders2API.getStationarySpells(p).upkeep();
         Ollivanders2API.getProphecies(p).upkeep();
         teleportSched();
         Ollivanders2API.getOwlPost(p).upkeep();
      }
      catch (Exception e)
      {
         common.printDebugMessage("Exceoption running scheduled tasks.", e, null, true);
      }

      // run invis player every second, offset from itemCurse schedule
      if (scheduleTimer % Ollivanders2Common.ticksPerSecond == 1)
      {
         invisPlayer();
      }

      // back up data hourly
      if (Ollivanders2.hourlyBackup && scheduleTimer % Ollivanders2Common.ticksPerHour == 0)
      {
         common.printDebugMessage("Saving plugin data...", null, null, false);

         p.savePluginData();
      }

      // Purge old plugin data hourly
      if(Ollivanders2.purgeBackupsAfter != 0 && scheduleTimer % Ollivanders2Common.ticksPerHour == 0) {
         common.printDebugMessage("Purging old plugin data...", null, null, false);

         p.purgeBackups(Ollivanders2.purgeBackupsAfter, null);
      }

      // Reset the timer so it does not grow unbounded, use >= just in case a tick gets missed somehow
      if (scheduleTimer >= timerReset)
         scheduleTimer = 1;
      else
         scheduleTimer = scheduleTimer + 1;
   }

   /**
    * Scheduling method that calls checkEffect() on all SpellProjectile objects
    * and removes those that have kill set to true.
    */
   private void projectileSched ()
   {
      List<O2Spell> projectiles = p.getProjectiles();
      List<O2Spell> projectiles2 = new ArrayList<>(projectiles);
      if (projectiles2.size() > 0)
      {
         for (O2Spell proj : projectiles2)
         {
            proj.checkEffect();
            if (proj.isKilled())
            {
               p.removeProjectile(proj);
            }
         }
      }
   }

   /**
    * Scheduling method that calls checkEffect on all OEffect objects associated with every online player
    * and removes those that have kill set to true.
    */
   private void oeffectSched ()
   {
      List<Player> onlinePlayers = new ArrayList<>();

      for (World world : p.getServer().getWorlds())
      {
         onlinePlayers.addAll(world.getPlayers());
      }

      for (Player player : onlinePlayers)
      {
         UUID pid = player.getUniqueId();

         Ollivanders2API.getPlayers().playerEffects.upkeep(pid);
      }
   }

   /**
    * Hides a player with the Cloak of Invisibility from other players.
    * Also hides players in Repello Muggleton from players not in that same spell.
    * Also sets any Creature targeting this player to have null target.
    */
   private void invisPlayer ()
   {
      Set<REPELLO_MUGGLETON> repelloMuggletons = new HashSet<>();
      for (O2StationarySpell stat : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         if (stat instanceof REPELLO_MUGGLETON)
         {
            repelloMuggletons.add((REPELLO_MUGGLETON) stat);
         }
      }

      for (Player player : p.getServer().getOnlinePlayers())
      {
         O2Player o2p = p.getO2Player(player);
         if (o2p == null)
            continue;

         boolean alreadyInvis = o2p.isInvisible();
         boolean alreadyInRepelloMuggleton = o2p.isInRepelloMuggleton();
         boolean hasCloak = hasCloak(player);

         if (hasCloak)
         {
            if (!alreadyInvis)
            {
               o2p.setInvisible(true);
               player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            }
         }

         boolean inRepelloMuggletons = false;
         for (O2StationarySpell stat : repelloMuggletons)
         {
            if (stat.isInside(player.getLocation()))
            {
               inRepelloMuggletons = true;
               if (!alreadyInRepelloMuggleton)
               {
                  o2p.setInRepelloMuggleton(true);
               }
               break;
            }
         }

         //TODO check the logic on this, I am not sure it is right
         if (hasCloak || inRepelloMuggletons)
         {
            for (Player player2 : p.getServer().getOnlinePlayers()) {
               if (player2.isPermissionSet("Ollivanders2.BYPASS") && player2.hasPermission("Ollivanders2.BYPASS")) {
                  continue;
               }

               O2Player viewer = p.getO2Player(player2);
               if (viewer == null)
                  continue;

               if (hasCloak)
               {
                  player2.hidePlayer(p, player);
                  System.out.println(player2.canSee(player));
               }
               else if (viewer.isMuggle())
               {
                  player2.hidePlayer(p, player);
               }
            }
         }
         else if (!hasCloak && alreadyInvis) {
            for (Player player2 : p.getServer().getOnlinePlayers())
            {
               O2Player viewer = Ollivanders2API.getPlayers().getPlayer(player2.getUniqueId());
               if (viewer == null)
                  continue;

               if (!inRepelloMuggletons && viewer.isMuggle()) {
                  player2.showPlayer(p, player);
               }
            }
            o2p.setInvisible(false);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
         }
         else if (!inRepelloMuggletons && alreadyInRepelloMuggleton) {
            if (!hasCloak) {
               for (Player player2 : p.getServer().getOnlinePlayers()) {
                  player2.showPlayer(p, player);
               }
            }
            o2p.setInRepelloMuggleton(false);
         }

         if (o2p.isInvisible()) {
            for (Entity entity : player.getWorld().getEntities()) {
               if (entity instanceof Creature) {
                  Creature creature = (Creature) entity;
                  if (creature.getTarget() == player) {
                     creature.setTarget(null);
                  }
               }
            }
         }

         p.setO2Player(player, o2p);
      }
   }

   /**
    * Does the player have the Cloak of Invisibility
    *
    * @param player - Player to be checked
    * @return - True if yes, false if no
    */
   private boolean hasCloak (@NotNull Player player)
   {
      ItemStack chestPlate = player.getInventory().getChestplate();
      if (chestPlate != null)
      {
         return O2ItemType.INVISIBILITY_CLOAK.isItemThisType(chestPlate);
      }
      return false;
   }

   /**
    * Handle all teleport events.
    */
   private void teleportSched()
   {
      List<Ollivanders2TeleportEvents.O2TeleportEvent> teleportEvents = p.getTeleportEvents();

      for (Ollivanders2TeleportEvents.O2TeleportEvent event : teleportEvents)
      {
         Player player = event.getPlayer();

         common.printDebugMessage("Teleporting " + player.getName(), null, null, false);

         Location currentLocation = event.getFromLocation();
         Location destination = event.getToLocation();
         destination.setPitch(currentLocation.getPitch());
         destination.setYaw(currentLocation.getYaw());

         try
         {
            player.teleport(destination);

            World curWorld = currentLocation.getWorld();
            World destWorld = destination.getWorld();
            if (curWorld == null || destWorld == null)
            {
               common.printDebugMessage("OllvandersSchedule.teleportSched: world is null", null, null, true);
            }
            else
            {
               if (event.isExplosionOnTeleport())
               {
                  curWorld.createExplosion(currentLocation, 0);
                  destWorld.createExplosion(destination, 0);
               }
            }
         }
         catch (Exception e)
         {
            common.printDebugMessage("Failed to teleport player.", e, null, true);
         }

         p.removeTeleportEvent(event);
      }
   }
}
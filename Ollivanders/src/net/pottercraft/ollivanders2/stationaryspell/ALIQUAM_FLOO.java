package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Stays in the fireplace and makes the floo network work
 *
 * @author lownes
 * @author Azami7
 */
public class ALIQUAM_FLOO extends O2StationarySpell
{
   private String flooName;
   private int countDown = 0;

   private final String flooNameLabel = "name";

   private HashMap<Player, Location> destinations = new HashMap<>();

   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public ALIQUAM_FLOO(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.ALIQUAM_FLOO;
   }

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param pid      the player who cast the spell
    * @param location the center location of the spell
    * @param type     the type of this spell
    * @param radius   the radius for this spell
    * @param duration the duration of the spell
    * @param flooName the name of this floo location
    */
   public ALIQUAM_FLOO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration, @NotNull String flooName)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.ALIQUAM_FLOO;
      this.flooName = flooName;

      common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
   }

   /**
    * Check for players activating the floo
    */
   @Override
   public void checkEffect ()
   {
      Block block = location.getBlock();
      if (block.getType().isSolid())
      {
         kill();
      }
      if (countDown > 0)
      {
         World world = location.getWorld();
         if (world == null)
         {
            kill();
            return;
         }

         world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);

         for (LivingEntity live : getCloseLivingEntities())
         {
            live.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 0));
         }
         countDown = countDown - 1;
      }
      if (block.getType() == Material.FIRE)
      {
         World world = location.getWorld();
         if (world == null)
         {
            kill();
            return;
         }

         for (Item item : location.getWorld().getEntitiesByClass(Item.class))
         {
            ItemStack stack = item.getItemStack();
            if (item.getLocation().getBlock().equals(block))
            {
               if (stack.getType() == Ollivanders2.flooPowderMaterial)
               {
                  if (stack.hasItemMeta())
                  {
                     ItemMeta meta = stack.getItemMeta();

                     if (meta != null && meta.hasLore())
                     {
                        List<String> lore = meta.getLore();
                        if (lore != null && lore.contains("Glittery, silver powder"))
                        {
                           countDown += 20 * 60 * stack.getAmount();
                           item.remove();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * Get the name of this floo location
    * @return
    */
   public String getFlooName ()
   {
      return flooName;
   }

   /**
    * Is it accepting floo destinations?
    *
    * @return true if this floo destination is online, false otherwise
    */
   public boolean isWorking ()
   {
      return countDown > 0;
   }

   /**
    * Stop the floo fireplace working after teleporting.
    */
   public void stopWorking ()
   {
      countDown = 0;
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   @Override
   @NotNull
   public Map<String, String> serializeSpellData ()
   {
      Map<String, String> spellData = new HashMap<>();

      spellData.put(flooNameLabel, flooName);

      return spellData;
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData(@NotNull Map<String, String> spellData)
   {
      for (Entry<String, String> e : spellData.entrySet())
      {
         if (e.getKey().equals(flooNameLabel))
         {
            flooName = e.getValue();
         }
      }
   }

   /**
    * Handle player floo chat
    *
    * @param event the event
    */
   @Override
   void doOnAsyncPlayerChatEvent (@NotNull AsyncPlayerChatEvent event)
   {
      Player player = event.getPlayer();
      String chat = event.getMessage();
      Location destination = null;

      if (!(player.getLocation().getBlock().equals(getBlock())) || !isWorking())
         return;

      List<ALIQUAM_FLOO> flooNetworkLocations = new ArrayList<>();

      // look for the destination in the registered floo network
      for (O2StationarySpell stationary : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         if (stationary instanceof ALIQUAM_FLOO)
         {
            ALIQUAM_FLOO aliquam = (ALIQUAM_FLOO) stationary;

            flooNetworkLocations.add(aliquam);
            if (aliquam.getFlooName().equalsIgnoreCase(chat.trim()))
            {
               destination = aliquam.location;
            }
         }
      }

      if (destination == null)
      {
         int randomIndex = Math.abs(Ollivanders2Common.random.nextInt() % flooNetworkLocations.size());
         destination = flooNetworkLocations.get(randomIndex).location;
      }

      destinations.put(player, destination);

      new BukkitRunnable()
      {
         @Override
         public void run()
         {
            if (!event.isCancelled())
            {
               stopWorking();
               doFlooTeleport(player);
            }
         }
      }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
   }

   /**
    * Do the teleport event.
    *
    * @param player the player to teleport
    */
   private void doFlooTeleport (Player player)
   {
      if (destinations.containsKey(player))
         p.addTeleportEvent(player, player.getLocation(), destinations.get(player));

      stopWorking();
      destinations.remove(player);
   }

   /**
    * Handle entity combusting due to the fire block
    *
    * @param event the event
    */
   @Override
   void doOnEntityCombustEvent(@NotNull EntityCombustEvent event)
   {
      Entity entity = event.getEntity();
      Location entityLocation = entity.getLocation();

      if (isInside(entityLocation))
      {
         event.setCancelled(true);
         common.printDebugMessage("ALIQUAM_FLOO: canceled EntityCombustEvent", null, null, false);
      }
   }

   /**
    * Dont take damage in the fireplace
    *
    * @param event the event
    */
   @Override
   void doOnEntityDamageEvent (@NotNull EntityDamageEvent event)
   {
      Entity entity = event.getEntity();
      Location entityLocation = entity.getLocation();

      if (isInside(entityLocation))
      {
         event.setCancelled(true);
         common.printDebugMessage("ALIQUAM_FLOO: canceled EntityDamageEvent", null, null, false);
      }
   }
}

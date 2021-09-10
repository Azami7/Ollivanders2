package net.pottercraft.ollivanders2.stationaryspell;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Stays in the fireplace and makes the floo network work
 *
 * @author lownes
 */
public class ALIQUAM_FLOO extends O2StationarySpell
{
   private String flooName;
   private int countDown = 0;

   private final String flooNameLabel = "name";

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
         countDown--;
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
}

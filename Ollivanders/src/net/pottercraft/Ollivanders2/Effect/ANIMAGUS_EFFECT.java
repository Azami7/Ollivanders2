package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;

import me.libraryaddict.disguise.disguisetypes.watchers.*;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Player.O2Player;

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;

/**
 * Transforms an Animagus player in to their animal form.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class ANIMAGUS_EFFECT extends ShapeShiftSuper
{
   String colorVariant;

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public ANIMAGUS_EFFECT (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.ANIMAGUS_EFFECT;

      permanent = true;
   }

   @Override
   /**
    * If the player has not yet transformed, transform them.
    */
   protected void upkeep ()
   {
      if (!transformed && !kill)
      {
         transform();
      }
   }

   /**
    * Customize the animal form for the player based on their Animagus form.
    */
   @Override
   protected void customizeWatcher ()
   {
      O2Player o2p = p.getO2Player(p.getServer().getPlayer(targetID));

      if (form == null)
      {
         // set the details of their animal form
         form = o2p.getAnimagusForm();
         colorVariant = o2p.getAnimagusColor();
      }

      if (form == EntityType.OCELOT)
      {
         OcelotWatcher ocelotWatcher = (OcelotWatcher)watcher;
         Ocelot.Type type = Ocelot.Type.WILD_OCELOT;
         ocelotWatcher.isAdult();

         try
         {
            type = Ocelot.Type.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Ocelot.Type " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();
         }

         if (type != null)
         {
            ocelotWatcher.setType(type);
         }
      }
      else if (form == EntityType.WOLF)
      {
         WolfWatcher wolfWatcher = (WolfWatcher)watcher;
         DyeColor color = DyeColor.WHITE;
         wolfWatcher.isAdult();

         try
         {
            color = DyeColor.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse DyeColor " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();
         }

         if (color != null)
         {
            wolfWatcher.isTamed();
            wolfWatcher.setCollarColor(color);
         }
      }
      else if (form == EntityType.HORSE)
      {
         HorseWatcher horseWatcher = (HorseWatcher)watcher;
         horseWatcher.setStyle(Horse.Style.NONE);
         Horse.Color color = Horse.Color.WHITE;
         horseWatcher.setBaby();

         try
         {
            color = Horse.Color.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Horse.Color " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();
         }

         if (color != null)
         {
            horseWatcher.setColor(color);
         }
      }
      else if (Ollivanders2.mcVersionCheck() && form == EntityType.LLAMA)
      {
         LlamaWatcher llamaWatcher = (LlamaWatcher)watcher;
         Llama.Color color = Llama.Color.WHITE;
         llamaWatcher.setBaby();

         try
         {
            color = Llama.Color.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Llama.Color " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();
         }
         if (color != null)
         {
            llamaWatcher.setColor(color);
         }
      }
      else if (form == EntityType.COW || form == EntityType.DONKEY || form == EntityType.MULE || form == EntityType.SLIME || form == EntityType.POLAR_BEAR)
      {
         AgeableWatcher ageableWatcher = (AgeableWatcher)watcher;
         ageableWatcher.setBaby();
      }
   }
}
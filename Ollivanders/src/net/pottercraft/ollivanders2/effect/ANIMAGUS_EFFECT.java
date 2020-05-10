package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import me.libraryaddict.disguise.disguisetypes.RabbitType;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.LlamaWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.OcelotWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.RabbitWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

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

      transformed = false;
      permanent = true;


      O2Player o2p = Ollivanders2API.getPlayers().getPlayer(pid);
      if (o2p == null)
      {
         p.getLogger().info("o2player cannot be found");

         kill();
         return;
      }

      form = o2p.getAnimagusForm();
      colorVariant = o2p.getAnimagusColor();

      if (form == null)
      {
         p.getLogger().info("Unable to get animagus form for " + Ollivanders2API.getPlayers().getPlayer(pid).getPlayerName());
         kill();
      }
   }

   /**
    * If the player has not yet transformed, transform them.
    */
   @Override
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

         ocelotWatcher.setType(type);
      }
      else if (form == EntityType.RABBIT)
      {
         RabbitWatcher rabbitWatcher = (RabbitWatcher)watcher;
         RabbitType type = RabbitType.WHITE;
         rabbitWatcher.isAdult();

         try
         {
            type = RabbitType.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Rabbit.Type " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();
         }

         rabbitWatcher.setType(type);
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

         wolfWatcher.isTamed();
         wolfWatcher.setCollarColor(color);
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

         horseWatcher.setColor(color);
      }
      else if (form == EntityType.LLAMA)
      {
         LlamaWatcher llamaWatcher = (LlamaWatcher) watcher;
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

         llamaWatcher.setColor(color);
      } else if (form == EntityType.COW || form == EntityType.DONKEY || form == EntityType.MULE || form == EntityType.SLIME || form == EntityType.POLAR_BEAR)
      {
         AgeableWatcher ageableWatcher = (AgeableWatcher) watcher;
         ageableWatcher.setBaby();
      }
   }

   /**
    * Override set permanent to ensure no code can inadvertently make the animagus effect non-permanent.
    *
    * @param perm true if this is permanent, false otherwise
    */
   @Override
   public void setPermanent(boolean perm) { }
}
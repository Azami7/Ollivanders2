package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import me.libraryaddict.disguise.disguisetypes.RabbitType;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.LlamaWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.RabbitWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.CatWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PandaWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PolarBearWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.FoxWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PigWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SheepWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SpiderWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.ShulkerWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.TurtleWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.TraderLlamaWatcher;

import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;

import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Panda;

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
      // in case the variant doesn't work, this can be used to fix the value at the end of this method
      String correctedVariant = null;

      if (watcher instanceof CatWatcher)
      {
         CatWatcher catWatcher = (CatWatcher) watcher;
         Cat.Type color = Cat.Type.WHITE;

         try
         {
            color = Cat.Type.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Cat.Type " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();

            correctedVariant = color.toString();
         }

         catWatcher.setType(color);
         catWatcher.setCollarColor(O2Color.getRandomPrimaryDyeableColor().getDyeColor());
      }
      else if (watcher instanceof RabbitWatcher)
      {
         RabbitType color = RabbitType.WHITE;

         try
         {
            color = RabbitType.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Rabbit.Type " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();

            correctedVariant = color.toString();
         }

         ((RabbitWatcher) watcher).setType(color);
      }
      else if (watcher instanceof WolfWatcher)
      {
         DyeColor color = DyeColor.WHITE;

         try
         {
            color = DyeColor.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse DyeColor " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();

            correctedVariant = color.toString();
         }

         ((WolfWatcher) watcher).isTamed();
         ((WolfWatcher) watcher).setCollarColor(color);
      }
      else if (watcher instanceof HorseWatcher)
      {
         Horse.Color color = Horse.Color.WHITE;

         try
         {
            color = Horse.Color.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Horse.Color " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();

            correctedVariant = color.toString();
         }

         ((HorseWatcher) watcher).setColor(color);
         ((HorseWatcher) watcher).setStyle(Horse.Style.NONE);
      }
      else if (watcher instanceof LlamaWatcher || watcher instanceof TraderLlamaWatcher)
      {
         Llama.Color color = Llama.Color.WHITE;

         try
         {
            color = Llama.Color.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Llama.Color " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();

            correctedVariant = color.toString();
         }

         if (watcher instanceof LlamaWatcher)
            ((LlamaWatcher) watcher).setColor(color);
         else
            ((TraderLlamaWatcher) watcher).setColor(color);
      }
      else if (watcher instanceof PandaWatcher)
      {
         ((PandaWatcher) watcher).setMainGene(Panda.Gene.NORMAL);
         ((PandaWatcher) watcher).setSitting(false);
      }
      else if (watcher instanceof PolarBearWatcher)
      {
         ((PolarBearWatcher) watcher).setStanding(false);
      }
      else if (watcher instanceof CreeperWatcher)
      {
         ((CreeperWatcher) watcher).setIgnited(false);
         ((CreeperWatcher) watcher).setPowered(false);
      }
      else if (watcher instanceof FoxWatcher)
      {
         Fox.Type color = Fox.Type.RED;

         try
         {
            color = Fox.Type.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse Fox.Type " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();

            correctedVariant = color.toString();
         }

         ((FoxWatcher) watcher).setType(color);
         ((FoxWatcher) watcher).setSitting(false);
      }
      else if (watcher instanceof PigWatcher)
      {
         ((PigWatcher) watcher).setSaddled(false);
      }
      else if (watcher instanceof SheepWatcher)
      {
         DyeColor color = DyeColor.WHITE;

         try
         {
            color = DyeColor.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse DyeColor " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();

            correctedVariant = color.toString();
         }

         ((SheepWatcher) watcher).setColor(color);
      }
      else if (watcher instanceof SlimeWatcher)
      {
         ((SlimeWatcher) watcher).setSize(1);
      }
      else if (watcher instanceof SpiderWatcher)
      {
         ((SpiderWatcher) watcher).setClimbing(false);
      }
      else if (watcher instanceof ShulkerWatcher)
      {
         DyeColor color = DyeColor.WHITE;

         try
         {
            color = DyeColor.valueOf(colorVariant);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to parse DyeColor " + colorVariant);
            if (Ollivanders2.debug)
               e.printStackTrace();

            correctedVariant = color.toString();
         }

         ((ShulkerWatcher) watcher).setColor(color);
      }
      else if (watcher instanceof TurtleWatcher)
      {
         ((TurtleWatcher) watcher).setEgg(false);
      }

      if (watcher instanceof AgeableWatcher)
      {
         ((AgeableWatcher) watcher).setAdult();
      }

      // fix player's animagus color variant if needed
      if (correctedVariant != null)
      {
         Ollivanders2API.getPlayers().fixPlayerAnimagusColorVariant(targetID, correctedVariant);
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
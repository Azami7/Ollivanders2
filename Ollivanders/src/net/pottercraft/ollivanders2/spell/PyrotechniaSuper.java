package net.pottercraft.ollivanders2.spell;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;

/**
 * Shoots one or more fireworks in to the air.
 *
 * @author Azami7
 */
public abstract class PyrotechniaSuper extends Charms
{
   int maxFireworks;
   int fireworkCount;
   int fireworkPower = 2;
   List <org.bukkit.Color> fireworkColors = null;
   List <org.bukkit.Color> fadeColors = null;
   FireworkEffect.Type fireworkType = null;
   boolean hasTrails = false;
   boolean hasFade = false;
   boolean hasFlicker = false;
   boolean shuffleTypes = false;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PyrotechniaSuper ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PyrotechniaSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      fireworkCount = 0;
      maxFireworks = 1;
   }

   public void checkEffect ()
   {
      if (fireworkCount < maxFireworks)
      {
         move();
         Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);

         FireworkMeta meta = firework.getFireworkMeta();
         // make firework fly for 1 seconds
         meta.setPower(fireworkPower);

         FireworkEffect.Builder builder = FireworkEffect.builder();
         if (fireworkColors != null)
         {
            builder.withColor(fireworkColors);
         }
         else
         {
            builder.withColor(Color.WHITE);
         }

         if (shuffleTypes)
         {
            int rand = Ollivanders2Common.random.nextInt() % 4;
            if (rand == 0)
               fireworkType = Type.STAR;
            else if (rand == 1)
               fireworkType = Type.BALL_LARGE;
            else if (rand == 2)
               fireworkType = Type.BALL;
            else
               fireworkType = Type.BURST;
         }

         if (fireworkType != null)
         {
            builder.with(fireworkType);
         }
         else
         {
            builder.with(Type.BALL);
         }

         builder.flicker(hasFlicker);
         builder.trail(hasTrails);

         if (hasFade)
         {
            if (fadeColors != null)
            {
               builder.withFade(fadeColors);
            }
         }

         meta.addEffect(builder.build());
         firework.setFireworkMeta(meta);

         fireworkCount++;
      }
   }

   /**
    * Set the number of fireworks that can be cast based on the user's experience.
    *
    * @param max the base maximum
    */
   void setMaxFireworks (int max)
   {
      if (usesModifier > (max*10))
      {
         maxFireworks = max;
      }
      else
      {
         maxFireworks = (int)usesModifier / max;
      }

      if (maxFireworks < 1)
         maxFireworks = 1;
   }
}

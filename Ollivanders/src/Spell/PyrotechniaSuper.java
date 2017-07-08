package Spell;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
/**
 * Shoots one or more fireworks in to the air.
 *
 * @author Azami7
 */
public abstract class PyrotechniaSuper extends SpellProjectile implements Spell
{
   int maxFireworks = 1;
   int fireworkCount = 0;
   List <org.bukkit.Color> fireworkColors = null;
   List <org.bukkit.Color> fadeColors = null;
   FireworkEffect.Type fireworkType = null;
   boolean hasTrails = false;
   boolean hasFade = false;
   boolean hasFlicker = false;
   boolean shuffleTypes = false;

   public PyrotechniaSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      moveEffectData = 0;
   }

   public void checkEffect ()
   {
      if (fireworkCount < maxFireworks)
      {
         move();
         Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);

         FireworkMeta meta = firework.getFireworkMeta();
         // make firework fly for 2 seconds
         meta.setPower(4);

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
            int rand = Ollivanders2.random.nextInt() % 4;
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
   }
}

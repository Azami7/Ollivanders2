package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * Shoots yellow star fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public class BOTHYNUS extends PyrotechniaSuper
{
   public BOTHYNUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.YELLOW);
      fireworkType = Type.STAR;

      if (usesModifier > 100)
      {
         maxFireworks = 10;
      }
      else
      {
         maxFireworks = usesModifier / 10;
      }
   }

   /*
   public void checkEffect ()
   {
      move();
      Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
      FireworkMeta meta = firework.getFireworkMeta();
      FireworkEffect.Builder builder = FireworkEffect.builder();
      builder.withColor(Color.YELLOW);
      builder.with(Type.STAR);
      meta.addEffect(builder.build());
      // make firework fly for 2 seconds
      meta.setPower(4);
      firework.setFireworkMeta(meta);
   }
   */
}

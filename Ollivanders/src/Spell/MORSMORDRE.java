package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * Cast the dark mark in the sky.
 *
 * @author Azami7
 */
public class MORSMORDRE extends PyrotechniaSuper
{
   public MORSMORDRE (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.GREEN);
      fireworkType = Type.CREEPER;
      maxFireworks = 20;
   }

   /*
   public void checkEffect ()
   {
      move();
      Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
      FireworkMeta meta = firework.getFireworkMeta();
      FireworkEffect.Builder builder = FireworkEffect.builder();
      builder.withColor(Color.GREEN);
      builder.with(Type.CREEPER);
      meta.addEffect(builder.build());
      // make firework fly for 2 seconds
      meta.setPower(4);
      firework.setFireworkMeta(meta);
   }
   */
}

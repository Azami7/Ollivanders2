package Spell;

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
 * Created by Azami7 on 6/29/17.
 *
 * Shoots mutliple orange burst fireworks in to the air.
 *
 * @author Azami7
 */
public class COMETES extends SpellProjectile implements Spell
{
   public COMETES (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
      FireworkMeta meta = firework.getFireworkMeta();
      FireworkEffect.Builder builder = FireworkEffect.builder();
      builder.withColor(Color.ORANGE);
      builder.with(Type.BURST);
      meta.addEffect(builder.build());
      // make firework fly for 2 seconds
      meta.setPower(4);
      firework.setFireworkMeta(meta);
   }
}

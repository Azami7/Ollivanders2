package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
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
 * Shoots multiple yellow star fireworks in to the air.
 *
 * @author Azami7
 */
public class BOTHYNUS extends SpellProjectile implements Spell
{
   @SuppressWarnings("deprecation")
   public BOTHYNUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      moveEffectData = Material.FIREWORK.getId();
   }

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
}

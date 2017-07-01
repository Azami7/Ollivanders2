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
 * Created by Azami7 on 6/28/17.
 *
 * @author Azami7
 */
public class MORSMORDRE extends SpellProjectile implements Spell
{
   @SuppressWarnings("deprecation")
   public MORSMORDRE (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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
      builder.withColor(Color.GREEN);
      builder.with(Type.CREEPER);
      meta.addEffect(builder.build());
      // make firework fly for 2 seconds
      meta.setPower(4);
      firework.setFireworkMeta(meta);
   }
}

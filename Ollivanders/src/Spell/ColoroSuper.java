package Spell;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/30/17.
 *
 * @author Azami7
 */
public class ColoroSuper extends SpellProjectile implements Spell
{
   DyeColor color = DyeColor.WHITE;

   public ColoroSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();

      for (LivingEntity live : getLivingEntities(1))
      {
         if (live instanceof Sheep)
         {
            Sheep sheep = (Sheep) live;
            sheep.setColor(color);
            kill();
            return;
         }
      }
      if (getBlock().getType() != Material.AIR)
      {
         for (Block block : getBlocksInRadius(location, usesModifier))
         {
            if (block.getState().getData() instanceof Colorable)
            {
               BlockState bs = block.getState();
               Colorable colorable = (Colorable) bs.getData();
               colorable.setColor(color);
               bs.setData((MaterialData) colorable);
               bs.update();
               kill();
            }
         }
      }
   }
}

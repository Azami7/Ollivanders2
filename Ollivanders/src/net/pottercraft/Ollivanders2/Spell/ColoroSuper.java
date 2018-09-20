package net.pottercraft.Ollivanders2.Spell;

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

/**
 * Created by Azami7 on 6/30/17.
 *
 * Change a sheep or a colorable block to a specific color.
 *
 * @author Azami7
 */
public abstract class ColoroSuper extends Charms
{
   DyeColor color = DyeColor.WHITE;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ColoroSuper ()
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
   public ColoroSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();

      for (LivingEntity live : getLivingEntities(2))
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
         for (Block block : p.common.getBlocksInRadius(location, usesModifier))
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

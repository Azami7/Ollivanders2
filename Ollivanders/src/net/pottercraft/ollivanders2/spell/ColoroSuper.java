package net.pottercraft.ollivanders2.spell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/30/17.
 *
 * Change a sheep or a colorable block to a specific color.
 *
 * @author Azami7
 */
public abstract class ColoroSuper extends Charms
{
   O2Color color = O2Color.WHITE;

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

      // first try to recolor any sheep in range
      for (LivingEntity live : getLivingEntities(2))
      {
         if (live instanceof Sheep)
         {
            Sheep sheep = (Sheep) live;
            sheep.setColor(color.getDyeColor());
            kill();
            return;
         }
      }

      Block target = getBlock();
      if (O2Color.isColorable(target.getType()))
      {
         Material newColor = O2Color.changeColor(target.getType(), color);
         target.setType(newColor);

         kill();
         return;
      }
   }
}

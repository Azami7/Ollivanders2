package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;
import org.bukkit.block.Banner;

import net.pottercraft.Ollivanders2.O2Color;
import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.List;
import java.util.Set;

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
   ColoroSuper ()
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
   ColoroSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      setUsesModifier();
   }

   /**
    * Look for colorable entities or blocks, if one is found, change its color
    */
   @Override
   protected void doCheckEffect ()
   {
      // first try to recolor any sheep in range
      List<LivingEntity> entities = getLivingEntities(1.5);

      if (entities.size() > 0)
      {
         for (LivingEntity livingEntity : entities)
         {
            if (livingEntity instanceof Sheep)
            {
               Sheep sheep = (Sheep)livingEntity;
               sheep.setColor(color.getDyeColor());
               kill();
               return;
            }
         }
      }
      else if (hasHitTarget())
      {
         Block target = getTargetBlock();

         if (O2Color.isColorable(target.getType()))
         {
            Material newColor = O2Color.changeColor(target.getType(), color);
            target.setType(newColor);

            kill();
         }
      }
   }
}

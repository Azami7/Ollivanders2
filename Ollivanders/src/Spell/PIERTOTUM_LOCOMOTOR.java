package Spell;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Transfigures an iron golem from a block of iron, and snow golem from block of snow.
 *
 * @author lownes
 */
public class PIERTOTUM_LOCOMOTOR extends Transfiguration implements Spell
{
   public PIERTOTUM_LOCOMOTOR (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      if (Ollivanders2.debug)
         p.getLogger().info("PIERTOTUM_LOCOMOTOR: create");
   }

   public void checkEffect ()
   {
      if (!hasTransfigured())
      {
         move();
         Block block = getBlock();
         Material material = block.getType();

         if (material == Material.IRON_BLOCK || material == Material.SNOW_BLOCK)
         {
            EntityType entityType;
            if (material == Material.IRON_BLOCK)
               entityType = EntityType.IRON_GOLEM;
            else
               entityType = EntityType.SNOWMAN;

            block.setType(Material.AIR);
            FallingBlock falling = location.getWorld().spawnFallingBlock(location, new MaterialData(material));
            transfigureEntity(falling, entityType, null);
            kill = false;
         }
      }
      else
      {
         if (lifeTicks > 160)
         {
            kill = true;
            endTransfigure();
         }
         else
         {
            lifeTicks++;
         }
      }
   }
}
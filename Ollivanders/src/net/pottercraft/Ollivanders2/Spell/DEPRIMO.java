package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Turns all blocks in a radius into fallingBlock entities
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class DEPRIMO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DEPRIMO ()
   {
      super();

      flavorText.add("She had blasted a hole in the sitting-room floor. They fell like boulders, Harry still holding onto her hand for dear life, there as a scream from below and he glimpsed two men trying to get out of the way as vast quantities of rubble and broken furniture rained all around them from the shattered ceiling.");
      flavorText.add("The Blasting Charm");
      text = "Deprimo creates an immense downward pressure which will cause all blocks within a radius to fall like sand.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DEPRIMO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      Block center = getBlock();
      double radius = usesModifier / 2;
      List<Block> tempBlocks = p.getTempBlocks();
      if (center.getType() != Material.AIR)
      {
         for (Block block : getBlocksInRadius(location, radius))
         {
            if (!tempBlocks.contains(block) &&
                  block.getType() != Material.WATER && block.getType() != Material.LAVA &&
                  block.getType() != Material.STATIONARY_WATER && block.getType() != Material.STATIONARY_LAVA &&
                  block.getType() != Material.AIR && block.getType() != Material.BEDROCK && block.getType().isSolid())
            {
               MaterialData data = block.getState().getData();
               block.setType(Material.AIR);
               block.getWorld().spawnFallingBlock(block.getLocation(), data);
            }
         }
         kill();
      }
   }
}
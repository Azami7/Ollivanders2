package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
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

      spellType = O2SpellType.DEPRIMO;

      flavorText = new ArrayList<String>() {{
         add("She had blasted a hole in the sitting-room floor. They fell like boulders, Harry still holding onto her hand for dear life, there as a scream from below and he glimpsed two men trying to get out of the way as vast quantities of rubble and broken furniture rained all around them from the shattered ceiling.");
         add("The Blasting Charm");
      }};

      text = "Deprimo creates an immense downward pressure which will cause all blocks within a radius to fall like sand.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DEPRIMO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.DEPRIMO;
      setUsesModifier();

      materialBlackList.add(Material.WATER);
      materialBlackList.add(Material.LAVA);
   }

   @Override
   public void checkEffect ()
   {
      move();

      Block target = getBlock();

      if (target != null)
      {
         double radius = usesModifier / 2;

         List<Block> nearbyBlocks = Ollivanders2API.common.getBlocksInRadius(target.getLocation(), radius);

         for (Block block : nearbyBlocks)
         {
            if (materialBlackList.contains(block.getType()))
            {
               continue;
            }

            Location blockLocation = block.getLocation();
            BlockData blockData = block.getBlockData();
            target.setType(Material.AIR);
            blockLocation.getWorld().spawnFallingBlock(blockLocation, blockData);
         }
      }
   }
}
package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Turns all blocks in a radius into fallingBlock entities
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class DEPRIMO extends O2Spell
{
   private final double minRadius = 1.0;
   private final double maxRadius = 10.0;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DEPRIMO()
   {
      super();

      spellType = O2SpellType.DEPRIMO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
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
      branch = O2MagicBranch.CHARMS;

      // material black list
      materialBlackList.add(Material.WATER);
      materialBlackList.add(Material.LAVA);
      materialBlackList.add(Material.FIRE);

      for (Material material : Ollivanders2Common.unbreakableMaterials)
      {
         if (!materialBlackList.contains(material))
            materialBlackList.add(material);
      }

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.BUILD);

      initSpell();
   }

   /**
    *
    */
   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
         return;

      double radius = usesModifier / 10;
      if (radius < minRadius)
      {
         radius = minRadius;
      }
      else if (radius > maxRadius)
      {
         radius = maxRadius;
      }

      List<Block> nearbyBlocks = Ollivanders2API.common.getBlocksInRadius(location, radius);

      for (Block block : nearbyBlocks)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("block type is " + block.getType().toString());
         }

         if (materialBlackList.contains(block.getType()))
         {
            continue;
         }

         Location blockLocation = block.getLocation();
         BlockData blockData = block.getBlockData();
         block.setType(Material.AIR);

         blockLocation.getWorld().spawnFallingBlock(blockLocation, blockData);
      }

      kill();
   }
}
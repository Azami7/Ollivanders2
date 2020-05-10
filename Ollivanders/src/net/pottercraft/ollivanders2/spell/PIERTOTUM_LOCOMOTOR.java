package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;

import net.pottercraft.ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Transfigures an iron golem from a block of iron, and snow golem from block of snow.
 *
 * @author lownes
 * @author Azami7
 */
public final class PIERTOTUM_LOCOMOTOR extends Transfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PIERTOTUM_LOCOMOTOR ()
   {
      super();

      spellType = O2SpellType.PIERTOTUM_LOCOMOTOR;
      branch = O2MagicBranch.TRANSFIGURATION;

      flavorText = new ArrayList<String>() {{
         add("And all along the corridor the statues and suits of armour jumped down from their plinths, and from the echoing crashes from the floors above and below, Harry knew that their fellows throughout the castle had done the same... Cheering and yelling, the horde of moving statues stampeded past Harry; some of them smaller, others larger than life.");
      }};

      text = "Piertotum locomotor, if cast at an iron or snow block, will transfigure that block into an iron or snow golem. This transfiguration's duration depends on your experience.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PIERTOTUM_LOCOMOTOR (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PIERTOTUM_LOCOMOTOR;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();

      // world guard flags
      worldGuardFlags.add(Flags.MOB_SPAWNING);
   }

   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
      {
         return;
      }

      Block target = getTargetBlock();
      Material material = target.getType();
      BlockData targetBlockData = target.getBlockData();

      if (material == Material.IRON_BLOCK || material == Material.SNOW_BLOCK)
      {
         EntityType entityType;
         if (material == Material.IRON_BLOCK)
         {
            entityType = EntityType.IRON_GOLEM;
         }
         else
         {
            entityType = EntityType.SNOWMAN;
         }

         target.setType(Material.AIR);
         FallingBlock falling = location.getWorld().spawnFallingBlock(location, targetBlockData);
         transfigureEntity(falling, entityType, null);
      }

      kill();
   }
}
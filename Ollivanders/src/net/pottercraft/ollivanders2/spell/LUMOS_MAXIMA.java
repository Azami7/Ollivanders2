package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Creates a glowstone at the reticule that goes away after a time.
 *
 * @author azami7
 * @version Ollivanders2
 */
public final class LUMOS_MAXIMA extends O2Spell
{
   int lineLength;
   int duration;

   static int maxLineLength = 20;
   static int maxDuration = Ollivanders2Common.ticksPerSecond * 600; // 10 minutes
   static int minDuration = Ollivanders2Common.ticksPerSecond * 30; // 30 seconds

   ArrayList<Block> changedBlocks = new ArrayList<>();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LUMOS_MAXIMA ()
   {
      super();

      spellType = O2SpellType.LUMOS_MAXIMA;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("\"Light your wands, can’t you? And hurry, we have little time!\" -Griphook");
      }};

      text = "Lumos Maxima will spawn a glowstone at the impact site.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LUMOS_MAXIMA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LUMOS_MAXIMA;
      branch = O2MagicBranch.CHARMS;
      initSpell();

      // base line length on experience
      lineLength = 1 + (int) usesModifier / 10;
      if (lineLength > maxLineLength)
      {
         lineLength = maxLineLength;
      }

      // set duration based on experience, min 30 seconds
      duration = Ollivanders2Common.ticksPerSecond * (int) usesModifier;
      if (duration < minDuration)
      {
         duration = minDuration;
      }
      else if (duration > maxDuration)
      {
         duration = maxDuration;
      }

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.BUILD);
   }

   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
      {
         // if we have not hit a solid target and still have more line to draw, add a glowstone
         if (lineLength > 0)
         {
            Block curBlock = location.getBlock();

            if (curBlock.getType() == Material.AIR)
            {
               curBlock.setType(Material.GLOWSTONE);
               location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);

               changedBlocks.add(curBlock);
               p.addTempBlock(curBlock, Material.AIR);
            }
         }
         // if we have not hit a solid target but don't have more line left, stop the projectile
         else
         {
            stopProjectile();
         }
      }
      else
      {
         duration--;

         if (duration <= 0)
         {
            kill();
         }
      }
   }

   @Override
   public void revert ()
   {
      for (Block block : changedBlocks)
      {
         p.revertTempBlock(block);
      }

      changedBlocks.clear();
   }
}
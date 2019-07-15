package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Creates a line of glowstone that goes away after a time.
 *
 * @author lownes
 * @author Azami7
 */
public final class LUMOS_DUO extends Charms
{
   private List<Block> line = new ArrayList();

   private int lineLength = 0;
   private int maxLineLength = 5;

   /**
    * If this is not permanent, how long it should last. Default is 15 seconds.
    */
   int spellDuration = Ollivanders2Common.ticksPerSecond * 15;

   /**
    * Allows spell variants to change the duration of this spell.
    */
   double durationModifier = 1.0;

   /**
    * Max duration of this spell. Default is 5 minutes.
    */
   int maxDuration = Ollivanders2Common.ticksPerSecond * 300;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LUMOS_DUO ()
   {
      super();

      spellType = O2SpellType.LUMOS_DUO;

      flavorText = new ArrayList<String>() {{
         add("A variation of the Wand-Lighting Charm.");
      }};

      text = "Creates a stream of glowstone to light your way.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LUMOS_DUO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LUMOS_DUO;
      setUsesModifier();

      lifeTicks = (int) (-(usesModifier * 20));

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);

      // world guard flags
      worldGuardFlags.add(DefaultFlag.BUILD);
   }

   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
      {
         // wait 2 before starting to create the line
         if (lifeTicks < 2)
         {
            return;
         }

         if (lineLength < maxLineLength)
         {
            Block curBlock = location.getBlock();
            if (curBlock.getType() == Material.AIR)
            {
               curBlock.setType(Material.GLOWSTONE);
               line.add(curBlock);
               p.addTempBlock(curBlock, Material.AIR);
            }
            else
            {
               stopProjectile();
            }
         }
         else
         {
            stopProjectile();
         }
      }
      else
      {
         // check time to live on the spell
         if (spellDuration <= 0)
         {
            // spell duration is up, kill the spell
            kill();
         }
         else
         {
            spellDuration--;
         }
      }
   }

   @Override
   protected void revert ()
   {
      for (Block block : line)
      {
         p.revertTempBlock(block);
      }

      line.clear();
   }
}
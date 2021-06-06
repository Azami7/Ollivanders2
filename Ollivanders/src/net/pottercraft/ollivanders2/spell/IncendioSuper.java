package net.pottercraft.ollivanders2.spell;

import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Sets fire to blocks or living entities for an amount of time depending on the player's spell level.
 *
 * @author Azami7
 */
public abstract class IncendioSuper extends O2Spell
{
   private double lifeTime;
   boolean strafe = false;
   boolean burning = false;
   int radius = 1;
   int blockRadius = 1;
   int durationModifier = 1;
   int maxDuration = 30 * Ollivanders2Common.ticksPerSecond;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public IncendioSuper()
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
   public IncendioSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      lifeTime = usesModifier * durationModifier * Ollivanders2Common.ticksPerSecond;
      if (lifeTime > maxDuration)
         lifeTime = maxDuration;

      // world-guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.BUILD);
         worldGuardFlags.add(Flags.LIGHTER);
         worldGuardFlags.add(Flags.PVP);
         worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
      }
   }

   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
         return;

      if (burning)
      {
         lifeTime--;

         if (lifeTime <= 0)
            kill();
      }
      else
      {
         Block target = getTargetBlock();

         // blocks
         if (!strafe)
         {
            Block above = target.getRelative(BlockFace.UP);
            setBlockOnFire(above);
         }
         else
         {
            for (Block block : Ollivanders2API.common.getBlocksInRadius(target.getLocation(), blockRadius))
            {
               setBlockOnFire(block);
            }
         }

         // items
         List<Item> items = getItems(radius);
         for (Item item : items)
         {
            item.setFireTicks((int)lifeTime);

            if (!strafe)
            {
               break;
            }
         }

         // entities
         List<LivingEntity> living = getLivingEntities(radius);
         for (LivingEntity live : living)
         {
            live.setFireTicks((int)lifeTime);

            if (!strafe)
            {
               break;
            }
         }

         burning = true;
      }
   }

   /**
    * Set an air block to fire
    *
    * @param block the block to change
    */
   private void setBlockOnFire (Block block)
   {
      Material type = block.getType();
      if (type == Material.AIR)
      {
         block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);

         block.setType(Material.FIRE);
         changed.add(block);
      }
   }

   /**
    * Change fire blocks back to air
    */
   @Override
   public void revert ()
   {
      for (Block block : changed)
      {
         Material mat = block.getType();
         if (mat == Material.FIRE)
         {
            block.setType(Material.AIR);
         }
      }
   }
}
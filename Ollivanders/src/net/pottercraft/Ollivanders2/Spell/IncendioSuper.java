package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Sets fire to blocks or living entities for an amount of time depending on the player's spell level.
 *
 * @author Azami7
 */
public abstract class IncendioSuper extends Charms
{
   private double lifeTime;
   boolean strafe = false;
   boolean burning = false;
   int radius = 1;
   int blockRadius = 1;
   int distance = 1;
   int duration = 1;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public IncendioSuper ()
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

      lifeTime = usesModifier * Ollivanders2Common.ticksPerSecond;
      if (lifeTime > 120)
         lifeTime = 120;

      // world-guard flags
      worldGuardFlags.add(DefaultFlag.BUILD);
      worldGuardFlags.add(DefaultFlag.LIGHTER);
      worldGuardFlags.add(DefaultFlag.PVP);
      worldGuardFlags.add(DefaultFlag.DAMAGE_ANIMALS);
   }

   @Override
   public void checkEffect ()
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

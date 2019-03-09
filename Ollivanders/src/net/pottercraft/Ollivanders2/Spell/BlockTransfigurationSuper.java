package net.pottercraft.Ollivanders2.Spell;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.List;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * The super class for transfiguration of objects.  Not for use on players or entities.
 *
 * @since 2.2.5
 * @author Azami7
 */
public abstract class BlockTransfigurationSuper extends O2Spell
{
   /**
    * If the transfiguration has taken place or not.
    */
   private boolean isTransfigured = false;

   /**
    * A map of the transfigured blocks and their original types for use with revert()
    */
   private HashMap<Block, Material> originalBlocks = new HashMap<>();

   //
   // these should be set by each spell as needed
   //
   /**
    * If this is populated, any material type key will be changed to the value
    */
   protected HashMap<Material, Material> transfigurationMap = new HashMap<>();

   /**
    * The material type to change this block to.
    */
   Material transfigureType = Material.AIR;

   /**
    * Whether this transfiguration permanent or not.  Usually for Charms it is false and for Transfiguration it is true.
    */
   protected boolean permanent = true;

   /**
    * How many blocks out from the target are affects.  Usually for permanent spells this is 1.
    */
   protected int radius = 1;

   /**
    * Allows spell variants to change the radius of the spell.
    */
   double radiusModifier = 1.0;

   /**
    * If this is not permanent, how long it should last.
    */
   int spellDuration = 1200;

   /**
    * Allows spell variants to change the duration of this spell.
    */
   double durationModifier = 1.0;

   /**
    * A blacklist of Material types that will not be affected by this spell.  Only used if the whitelist is empty.
    */
   List<Material> materialBlacklist = new ArrayList<>();

   /**
    * A whitelist of Material types that will be affected by this spell.
    */
   List<Material> materialWhitelist = new ArrayList<>();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BlockTransfigurationSuper ()
   {
      super();

      branch = O2MagicBranch.TRANSFIGURATION;
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BlockTransfigurationSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.TRANSFIGURATION;

      materialBlacklist.add(Material.BEDROCK);
      materialBlacklist.add(Material.ENDER_CHEST);
      materialBlacklist.add(Material.BARRIER);
   }

   /**
    * If we have hit a target, transform it if it is not transformed or change it back if it is already transformed and
    * the duration is expired for a temporary spell.
    */
   @Override
   protected void doCheckEffect ()
   {
      if (hasHitTarget())
      {
         // if the object has not transfigured, transfigure it
         if (!isTransfigured)
         {
            Block target = getTargetBlock();
            if (target != null)
            {
               transfigure(target);

               if (!permanent)
               {
                  spellDuration = (int) (spellDuration * durationModifier);
               }
               else
               {
                  spellDuration = 0;
                  kill();
               }
            }
         }
         // if the entity has transfigured, check time to change back
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
   }

   /**
    * Transfigure the target block or blocks. Will not change the block if it is on the materialBlacklist list or if the
    * target block is already the transfigure type.
    */
   protected void transfigure (Block block)
   {
      if (isTransfigured)
      {
         return;
      }

      // get the objects to be transfigured
      for (Block b : Ollivanders2API.common.getBlocksInRadius(block.getLocation(), (int) (radius * radiusModifier)))
      {
         if (!canTransfigure(b))
         {
            continue;
         }

         Material orig = b.getType();
         // if not permanent, keep track of what the original block was
         if (!permanent)
         {
            originalBlocks.put(b, orig);
         }

         if (transfigurationMap.containsKey(orig))
         {
            b.setType(transfigurationMap.get(orig));
         }
         else
         {
            b.setType(transfigureType);
         }
      }

      isTransfigured = true;
   }

   /**
    * Determines if this block can be changed by this Transfiguration spell.
    *
    * @param b the block to check
    * @return true if the block can be changed, false otherwise.
    */
   private boolean canTransfigure (Block b)
   {
      // get block type
      Material m = b.getType();

      boolean canChange = true;

      if (m == transfigureType) // do not change if this block is already the target type
      {
         canChange = false;
      }
      else if (b.getState() instanceof Entity) // do not change if this block is an Entity
      {
         canChange = false;
      }
      else if (materialBlacklist.contains(m)) // do not change if this block is in the blacklist
      {
         canChange = false;
      }
      else if (!materialWhitelist.isEmpty()) // do not change if the whitelist exists and this block is not in it
      {
         if (!materialWhitelist.contains(m))
         {
            canChange = false;
         }
      }

      return canChange;
   }

   @Override
   public void revert ()
   {
      if (!permanent)
      {
         for (Entry<Block, Material> entry : originalBlocks.entrySet())
         {
            Block b = entry.getKey();
            Material m = entry.getValue();

            try
            {
               b.setType(m);
            }
            catch (Exception e)
            {
               // in case the blocks do not exist anymore.
            }
         }
      }
   }

   @Override
   public String getText ()
   {
      return text;
   }

   @Override
   public String getFlavorText()
   {
      if (flavorText.size() < 1)
      {
         return null;
      }
      else
      {
         int index = Math.abs(Ollivanders2Common.random.nextInt() % flavorText.size());
         return flavorText.get(index);
      }
   }

   @Override
   public O2MagicBranch getMagicBranch ()
   {
      return branch;
   }
}

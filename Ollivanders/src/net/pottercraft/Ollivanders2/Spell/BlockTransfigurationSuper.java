package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.List;

/**
 * The super class for transfiguration of objects.  Not for use on players or entities.
 *
 * @since 2.2.5
 * @author Azami7
 */
public abstract class BlockTransfigurationSuper extends SpellProjectile implements O2Spell
{
   //
   // these should generally not be changed
   //
   /**
    * If the transfiguration has taken place or not.
    */
   protected boolean isTransfigured = false;

   /**
    * A map of the transfigured blocks and their original types for use with revert()
    */
   protected HashMap<Block, Material> originalBlocks = new HashMap<>();

   //
   // these should be set by each spell as needed
   //
   /**
    * The branch of magic this spell is - most likely Charms or Transfiguration
    */
   protected O2MagicBranch branch = O2MagicBranch.TRANSFIGURATION;

   /**
    * If this is populated, any material type key will be changed to the value
    */
   protected HashMap<Material, Material> transfigurationMap = new HashMap<>();

   /**
    * The material type to change this block to.
    */
   protected Material transfigureType = Material.AIR;

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
   protected double radiusModifier = 1.0;

   /**
    * If this is not permanent, how long it should last.
    */

   protected int spellDuration = 1200;
   /**
    * Allows spell variants to change the duration of this spell.
    */
   protected double durationModifier = 1.0;

   /**
    * The current duration of this spell.
    */
   protected int lifeTicks = 0;

   /**
    * A blacklist of Material types that will not be affected by this spell.  Only used if the whitelist is empty.
    */
   protected List<Material> materialBlacklist = new ArrayList<>();

   /**
    * A whitelist of Material types that will be affected by this spell.
    */
   protected List<Material> materialWhitelist = new ArrayList<>();

   /**
    * Flavor text for this spell in spellbooks, etc.  Optional.
    */
   protected ArrayList<String> flavorText = new ArrayList<>();

   /**
    * The description text for this spell in spell books.  Required or spell cannot be written in a book.
    */
   protected String text = "";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BlockTransfigurationSuper () { }

   /**
    * Constructor for casting a transfiguration spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public BlockTransfigurationSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      materialBlacklist.add(Material.BEDROCK);
      materialBlacklist.add(Material.ENDER_CHEST);
   }

   @Override
   public void checkEffect()
   {
      // if the object has not transfigured, transfigure it
      if (!isTransfigured)
      {
         // move the projectile
         move();
         Block target = getTargetBlock();
         if (target != null)
         {
            // if the server is running WorldGuard, make sure the player has permissions
            if (Ollivanders2.worldGuardEnabled && !Ollivanders2.worldGuardO2.checkWGBuild(player, target.getLocation()))
            {
               kill();
               p.spellCannotBeCastMessage(player);
               return;
            }

            transfigure(target);

            if (!permanent)
            {
               spellDuration = (int) (spellDuration * durationModifier);
               kill = false;
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
         if (lifeTicks >= spellDuration)
         {
            // spell duration is up, transfigure back to original shape
            if (!permanent)
            {
               revert();
            }
            kill();
            return;
         }

         lifeTicks++;
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
      for (Block b : getBlocksInRadius(block.getLocation(), (int)(radius * radiusModifier)))
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
    * @param b
    * @return true if the block can be changed, false otherwise.
    */
   protected boolean canTransfigure (Block b)
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
         int index = Math.abs(Ollivanders2.random.nextInt() % flavorText.size());
         return flavorText.get(index);
      }
   }

   @Override
   public O2MagicBranch getMagicBranch ()
   {
      return branch;
   }
}

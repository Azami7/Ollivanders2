package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.material.MaterialData;

import net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * Moves a group of blocks.
 *
 * @author lownes
 * @author Azami7
 */
public final class WINGARDIUM_LEVIOSA extends Charms
{
   Map<Location, Material> materialMap = new HashMap();
   List<Block> blockList = new ArrayList();
   List<Location> locList = new ArrayList();
   boolean moving = true;
   double length = 0;
   boolean dropBlocks = true;   //If the blocks should be converted to fallingBlocks after the end of the spell.

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public WINGARDIUM_LEVIOSA ()
   {
      super();

      flavorText.add("The Levitation Charm");
      flavorText.add("You're saying it wrong ...It's Wing-gar-dium Levi-o-sa, make the 'gar' nice and long.\" -Hermione Granger");
      flavorText.add("The Levitation Charm is one of the first spells learnt by any young witch or wizard.  With the charm a witch or wizard can make things fly with the flick of a wand.");
      text = "Levitates and lets you move blocks while crouching.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public WINGARDIUM_LEVIOSA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      if (moving)
      {
         move();
         Material type = getBlock().getType();
         if (type != Material.AIR && type != Material.WATER && type != Material.STATIONARY_WATER
               && type != Material.LAVA && type != Material.STATIONARY_LAVA)
         {
            moving = false;
            double radius = usesModifier / 4;
            ArrayList<COLLOPORTUS> collos = new ArrayList<COLLOPORTUS>();
            for (StationarySpellObj stat : p.getStationary())
            {
               if (stat instanceof COLLOPORTUS)
               {
                  collos.add((COLLOPORTUS) stat);
               }
            }
            for (Block block : getBlocksInRadius(location, radius))
            {
               boolean insideCollo = false;
               for (COLLOPORTUS collo : collos)
               {
                  if (collo.isInside(block.getLocation()))
                  {
                     insideCollo = true;
                  }
               }
               if (!insideCollo)
               {
                  type = block.getType();
                  if (type != Material.WATER && type != Material.STATIONARY_WATER && type != Material.LAVA
                        && type != Material.STATIONARY_LAVA && type != Material.SAND && type != Material.GRAVEL
                        && type != Material.AIR && type != Material.BEDROCK && type.isSolid()
                        && !p.getTempBlocks().contains(block))
                  {
                     Location loc = centerOfBlock(block).subtract(location);
                     Material mat = block.getType();
                     materialMap.put(loc, mat);
                     locList.add(loc);
                     blockList.add(block);
                  }
               }
            }
            length = player.getEyeLocation().distance(location);
            kill = false;
         }
      }
      else
      {
         if (player.isSneaking())
         {
            List<Location> locList2 = new ArrayList<Location>(locList);
            for (Block block : blockList)
            {
               if (block.getType() == Material.AIR)
               {
                  locList2.remove(locList.get(blockList.indexOf(block)));
               }
            }
            locList = locList2;
            for (Block block : blockList)
            {
               block.setType(Material.AIR);
            }
            blockList.clear();
            for (Location loc : locList)
            {
               Vector direction = player.getEyeLocation().getDirection().multiply(length);
               Location center = player.getEyeLocation().add(direction);
               location = center;
               Location toLoc = center.clone().add(loc);
               if (toLoc.getBlock().getType() == Material.AIR)
               {
                  toLoc.getBlock().setType(materialMap.get(loc));
                  blockList.add(toLoc.getBlock());
               }
            }
         }
         else if (dropBlocks)
         {
            for (Block block : blockList)
            {
               block.setType(Material.AIR);
            }
            Vector direction = player.getEyeLocation().getDirection().multiply(length);
            Location center = player.getEyeLocation().add(direction);
            Vector moveVec = center.toVector().subtract(location.toVector());
            for (Location loc : locList)
            {
               Location toLoc = center.clone().add(loc);
               MaterialData material = new MaterialData(materialMap.get(loc));
               FallingBlock fall = loc.getWorld().spawnFallingBlock(toLoc, material);
               fall.setVelocity(moveVec);
            }
            kill();
         }
         else
         {
            kill();
         }
      }
   }

   /**
    * Returns the location at the center of the block, instead of the corner.
    *
    * @param block - Block to get the center location of.
    * @return Location at the center of the block.
    */
   private Location centerOfBlock (Block block)
   {
      Location newLoc = block.getLocation().clone();
      newLoc.setX(newLoc.getX() + 0.5);
      newLoc.setY(newLoc.getY() + 0.5);
      newLoc.setZ(newLoc.getZ() + 0.5);
      return newLoc;
   }
}
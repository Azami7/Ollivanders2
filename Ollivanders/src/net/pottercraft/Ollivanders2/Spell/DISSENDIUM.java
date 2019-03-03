package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens a trapdoor
 *
 * @author Azami7
 */
public final class DISSENDIUM extends Charms
{
   private double maxOpenTime;
   private int openTime;
   private boolean isOpen;
   private Block trapDoorBlock;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DISSENDIUM ()
   {
      super();

      spellType = O2SpellType.DISSENDIUM;

      flavorText = new ArrayList<String>() {{
         add("The Opening Charm");
         add("At once, the statue's hump opened wide enough to admit a fairly thin person.");
      }};

      text = "Dissendium will open a door or trapdoor for a few seconds. To open a door, aim at the bottom half.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DISSENDIUM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.DISSENDIUM;
      setUsesModifier();

      maxOpenTime = usesModifier * 20;

      p.getLogger().info("dissendium max open time is " + maxOpenTime);

      openTime = 0;
      isOpen = false;
   }

   /**
    * Move the spell projectile until it hits a block, if that is a trapdoor, open it and keep it open until the max
    * duration of the spell is reached.
    */
   @Override
   public void checkEffect ()
   {
      // continue until the spell opens a trapdoor, hits another block type and is killed, or projectile expires
      if (!isOpen)
      {
         move();

         Block target = getBlock();
         BlockData targetBlockData = target.getBlockData();

         if (targetBlockData instanceof TrapDoor)
         {
            openTrapDoor();
         }
      }
      else
      {
         // count down the open time
         openTime++;

         p.getLogger().info("open time = " + openTime);

         if (openTime >= maxOpenTime)
         {
            closeTrapDoor();
            kill();
         }
      }
   }

   /**
    * Opens the trapdoor at target block
    */
   private void openTrapDoor ()
   {
      Block target = getBlock();

      // check for colloportus spell locking this door
      Location targetLocation = target.getLocation();
      List<StationarySpellObj> spellsAtLocation = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(targetLocation);

      for (StationarySpellObj statSpell : spellsAtLocation)
      {
         if (statSpell instanceof COLLOPORTUS)
         {
            p.getLogger().info("trapdoor in a colloportus");
            kill();
            return;
         }
      }

      BlockData targetBlockData = target.getBlockData();

      // check to see if the trap door is already open
      if (((TrapDoor) targetBlockData).isOpen())
      {
         p.getLogger().info("trapdoor already open");
         kill();
         return;
      }

      trapDoorBlock = target;
      ((TrapDoor) targetBlockData).setOpen(true);
      trapDoorBlock.setBlockData(targetBlockData);

      isOpen = true;
      kill = false;
   }

   /**
    * Close the trap door
    */
   private void closeTrapDoor ()
   {
      if (!isOpen)
      {
         return;
      }

      // close the trap door
      TrapDoor trapDoorData = (TrapDoor) trapDoorBlock.getBlockData();
      trapDoorData.setOpen(false);
      trapDoorBlock.setBlockData(trapDoorData);

      isOpen = false;
   }
}

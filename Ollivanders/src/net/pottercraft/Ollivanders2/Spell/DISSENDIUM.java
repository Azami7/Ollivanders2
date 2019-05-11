package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
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
 * @author lownes
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

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      maxOpenTime = usesModifier * Ollivanders2Common.ticksPerSecond;

      openTime = 0;
      isOpen = false;

      // world guard flags
      worldGuardFlags.add(DefaultFlag.USE);
   }

   /**
    * Move the spell projectile until it hits a block, if that is a trapdoor, open it and keep it open until the max
    * duration of the spell is reached.
    */
   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
         return;

      // continue until the spell opens a trapdoor, hits another block type and is killed, or projectile expires
      if (isOpen)
      {
         // count down the open time
         openTime++;

         // if the open time has expired, close the trap door and kill the spell
         if (openTime >= maxOpenTime)
         {
            closeTrapDoor();
            kill();
         }
      }
      else
      {
         Block target = getTargetBlock();
         BlockData targetBlockData = target.getBlockData();

         // if the target is a trap door, open it
         if (targetBlockData instanceof TrapDoor)
         {
            openTrapDoor();
         }

         // kill the spell if we did not open a trap door after hitting a target
         if (!isOpen)
            kill();
      }
   }

   /**
    * Opens the trapdoor at target block
    */
   private void openTrapDoor ()
   {
      Block target = getTargetBlock();

      // check for colloportus spell locking this door
      Location targetLocation = target.getLocation();
      List<StationarySpellObj> spellsAtLocation = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(targetLocation);

      for (StationarySpellObj statSpell : spellsAtLocation)
      {
         if (statSpell instanceof COLLOPORTUS)
         {
            kill();
            return;
         }
      }

      BlockData targetBlockData = target.getBlockData();

      // check to see if the trap door is already open
      if (((TrapDoor) targetBlockData).isOpen())
      {
         kill();
         return;
      }

      trapDoorBlock = target;
      ((TrapDoor) targetBlockData).setOpen(true);
      trapDoorBlock.setBlockData(targetBlockData);

      isOpen = true;
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

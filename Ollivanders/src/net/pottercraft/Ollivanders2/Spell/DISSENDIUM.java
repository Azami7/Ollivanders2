package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

/**
 * Opens a trapdoor or door.
 *
 * @author lownes
 * @author Azami7
 */
public final class DISSENDIUM extends Charms
{
   private double lifeTime;
   private boolean move;
   private int openTime;
   private boolean open;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DISSENDIUM ()
   {
      super();

      flavorText.add("The Opening Charm");
      flavorText.add("At once, the statue's hump opened wide enough to admit a fairly thin person.");
      text = "Dissendium will open a door or trapdoor for a few seconds. To open a door, aim at the bottom half.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DISSENDIUM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      lifeTime = usesModifier * 16;
      move = true;
      openTime = 160;
      open = true;
   }

   public void checkEffect ()
   {
      if (move)
      {
         move();
         if (p.isInsideOf(StationarySpells.COLLOPORTUS, location))
         {
            kill();
            return;
         }
         if (getBlock().getState().getData() instanceof Openable)
         {
            kill = false;
            move = false;
            open = ((Openable) getBlock().getState().getData()).isOpen();
         }
      }
      else
      {
         openTime--;
         BlockState state = getBlock().getState();
         Openable openable = (Openable) state.getData();
         if (openTime > 0)
         {
            openable.setOpen(!open);
            state.setData((MaterialData) openable);
            state.update();
         }
         else
         {
            openable.setOpen(open);
            state.setData((MaterialData) openable);
            state.update();
            kill();
         }
      }
      if (lifeTicks > lifeTime)
      {
         kill();
      }
   }
}

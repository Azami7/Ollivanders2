package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

/**
 * Locks blocks in to place.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class COLLOPORTUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLLOPORTUS ()
   {
      super();

      flavorText.add("The Locking Spell.");
      text = "Locks blocks in to place.  This spell does not age and can only be removed with the Unlocking Spell, Alohomora.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public COLLOPORTUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS total = new net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS(player, location,
               StationarySpells.COLLOPORTUS, 5, duration);
         total.flair(10);
         p.addStationary(total);
         kill();
      }
   }
}
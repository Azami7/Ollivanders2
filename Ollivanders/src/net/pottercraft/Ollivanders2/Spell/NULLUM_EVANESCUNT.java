package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

/**
 * Makes an anti-disapparition spell. Players can't apparate out of it.
 *
 * @author lownes
 */
public final class NULLUM_EVANESCUNT extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public NULLUM_EVANESCUNT ()
   {
      super();

      text = "Nullum evanescunt creates a stationary spell which will not allow disapparition out of it.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public NULLUM_EVANESCUNT (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.NULLUM_EVANESCUNT nullum = new net.pottercraft.Ollivanders2.StationarySpell.NULLUM_EVANESCUNT(player, location, StationarySpells.NULLUM_EVANESCUNT, 5, duration);
         nullum.flair(10);
         p.addStationary(nullum);
         kill();
      }
   }
}
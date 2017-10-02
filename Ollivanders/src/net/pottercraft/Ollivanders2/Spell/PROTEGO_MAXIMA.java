package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;
import org.bukkit.Material;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

/**
 * Makes a spell projectile that creates a shield that hurts any entities within 0.5 meters of the spell wall.
 *
 * @author lownes
 * @author Azami7
 */
public final class PROTEGO_MAXIMA extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_MAXIMA ()
   {
      super();

      flavorText.add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" -Filius Flitwick");
      flavorText.add("A Stronger Shield Charm");

      text = "Protego maxima is a stationary spell which will hurt any entities close to it's boundary.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public PROTEGO_MAXIMA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         double damage = usesModifier / 10;
         net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_MAXIMA max =
               new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_MAXIMA(player, location, StationarySpells.PROTEGO_MAXIMA, 5, duration, damage);
         max.flair(10);
         p.addStationary(max);
         kill();
      }
   }
}
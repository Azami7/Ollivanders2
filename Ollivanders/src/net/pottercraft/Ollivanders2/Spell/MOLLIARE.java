package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

/**
 * Eliminates all fall damage.
 *
 * @author lownes
 * @author Azami7
 */
public final class MOLLIARE extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MOLLIARE ()
   {
      super();

      flavorText.add("The Cushioning Charm.");
      flavorText.add("Harry felt himself glide back toward the ground as though weightless, landing painlessly on the rocky passage floor.");

      text = "Molliare softens the ground in a radius around the site.  All fall damage will be negated in this radius.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public MOLLIARE (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.MOLLIARE molliare = new net.pottercraft.Ollivanders2.StationarySpell.MOLLIARE(player, location, StationarySpells.MOLLIARE, 5, duration);
         molliare.flair(10);
         p.addStationary(molliare);
         kill();
      }
   }
}
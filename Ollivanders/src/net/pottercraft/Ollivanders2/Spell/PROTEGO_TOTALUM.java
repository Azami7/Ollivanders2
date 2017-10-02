package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;
import org.bukkit.Material;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

/**
 * Creates a PROTEGO_TOTALUM Stationary Spell Object
 *
 * @author lownes
 * @author Azami7
 */
public final class PROTEGO_TOTALUM extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_TOTALUM ()
   {
      super();

      flavorText.add("Raising her wand, she began to walk in a wide circle around Harry and Ron, murmuring incantations as she went. Harry saw little disturbances in the surrounding air: it was as if Hermione had cast a heat haze across their clearing.");
      text = "Protego totalum is a stationary spell which will prevent any entities from crossing it's boundary.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public PROTEGO_TOTALUM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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
         net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_TOTALUM total =
               new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_TOTALUM(player, location, StationarySpells.PROTEGO_TOTALUM, 5, duration);
         total.flair(10);
         p.addStationary(total);
         kill();
      }
   }
}
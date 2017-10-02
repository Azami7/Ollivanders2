package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Transfigures entity into a parrot (MC >= 1.12) or bat (MC < 1.12)
 *
 * @see MetatrepoSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class AVIFORS extends MetatrepoSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AVIFORS ()
   {
      super();

      flavorText.add("However, mastering a Transfiguration spell such as \"Avifors\" can be both rewarding and useful.");
      text = "Turns target entity in to an owl.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public AVIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      if (Ollivanders2.mcVersionCheck())
         animalShape = EntityType.PARROT;
      else
         animalShape = EntityType.BAT;
   }
}
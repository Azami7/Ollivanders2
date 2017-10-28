package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Transfigures entity into a rabbit.
 *
 * @since 2.2.5
 * @link https://github.com/Azami7/Ollivanders2/issues/51
 * @see MetatrepoSuper
 * @author Azami7
 */
public final class LAPIFORS extends MetatrepoSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LAPIFORS ()
   {
      super();

      flavorText.add("\"Lapifors, the transformation of a small object into a rabbit\" -Hermione Granger");

      text = "The transfiguration spell Lapifors will transfigure an entity into a chicken.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LAPIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.RABBIT;
   }
}

package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Transfigures entity into a chicken.
 *
 * @see MetatrepoSuper
 * @version Ollivanders2
 * @author Azami7
 */
public final class DUCKLIFORS extends MetatrepoSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DUCKLIFORS ()
   {
      super();

      text = "The transfiguration spell Ducklifors will transfigure an entity into a chicken.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DUCKLIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.CHICKEN;
   }
}
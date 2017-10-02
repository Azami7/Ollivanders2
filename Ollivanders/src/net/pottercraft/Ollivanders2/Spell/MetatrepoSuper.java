package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 7/1/17.
 *
 * Turn target entity in to a specific creature.
 *
 * @author Azami7
 */
public abstract class MetatrepoSuper extends Transfiguration
{
   EntityType animalShape;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MetatrepoSuper () { }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public MetatrepoSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      simpleTransfigure(animalShape, null);
   }
}

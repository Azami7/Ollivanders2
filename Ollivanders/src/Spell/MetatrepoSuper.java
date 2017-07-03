package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Created by Azami7 on 7/1/17.
 *
 * Turn target entity in to a specific creature.
 *
 * @author Azami7
 */
public abstract class MetatrepoSuper extends Transfiguration implements Spell
{
   EntityType animalShape;

   public MetatrepoSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      simpleTransfigure(animalShape, null);
   }
}

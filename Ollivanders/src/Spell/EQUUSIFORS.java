package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Transfigures an item into a horse.
 *
 * @see MetatrepoSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class EQUUSIFORS extends MetatrepoSuper
{

   public EQUUSIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.HORSE;
   }
}
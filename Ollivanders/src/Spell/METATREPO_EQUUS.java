package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Transfigures an item into a horse.
 *
 * @author lownes
 * @author Azami7
 */
public class METATREPO_EQUUS extends MetatrepoSuper
{

   public METATREPO_EQUUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.HORSE;
   }
}
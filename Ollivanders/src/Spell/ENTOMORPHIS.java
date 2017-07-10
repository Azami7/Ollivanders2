package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;

/**
 * Turn target entity in to a Silverfish.
 *
 * @author lownes
 * @author Azami7
 */
public class ENTOMORPHIS extends MetatrepoSuper
{
   public ENTOMORPHIS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.SILVERFISH;
   }
}
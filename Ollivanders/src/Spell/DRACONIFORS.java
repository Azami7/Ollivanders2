package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;

/**
 * Turn target entity in to a Dragon. OllivandersPlayerListener.draconiforsBlockChange()
 * keeps any transfigured dragons from destroying terrain.
 *
 * @see MetatrepoSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class DRACONIFORS extends MetatrepoSuper
{
   public DRACONIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.ENDER_DRAGON;
   }
}
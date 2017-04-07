package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Vanishes an entity. The entity will reappear after a certain time.
 *
 * @author lownes
 */
public class EVANESCO extends Transfiguration implements Spell
{

   public EVANESCO (Ollivanders2 plugin, Player player, Spells name,
                    Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      simpleTransfigure(null, null);
   }

}
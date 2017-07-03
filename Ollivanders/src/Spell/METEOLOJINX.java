package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Creates a storm of a variable duration.
 *
 * @version Ollivanders2
 * @see MeteolojinxSuper
 * @author lownes
 * @author Azami7
 */
public class METEOLOJINX extends MeteolojinxSuper
{
   public METEOLOJINX (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      storm = true;
   }
}
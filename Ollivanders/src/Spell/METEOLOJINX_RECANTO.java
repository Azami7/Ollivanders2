package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Ends a storm for a variable duration
 *
 * @author lownes
 */
public class METEOLOJINX_RECANTO extends MeteolojinxSuper
{
   public METEOLOJINX_RECANTO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      storm = false;
   }
}
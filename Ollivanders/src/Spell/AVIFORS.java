package Spell;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Transfigures entity into a parrot (MC >= 1.12) or bat (MC < 1.12)
 *
 * @see MetatrepoSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class AVIFORS extends MetatrepoSuper
{
   public AVIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      String mcVersion = Bukkit.getBukkitVersion();
      if (Ollivanders2.mcVersionCheck())
         animalShape = EntityType.PARROT;
      else
         animalShape = EntityType.BAT;
   }
}
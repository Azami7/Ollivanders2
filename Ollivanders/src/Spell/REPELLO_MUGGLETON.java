package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpells;

/**
 * Adds a repello muggleton stationary spell object
 *
 * @author lownes
 */
public class REPELLO_MUGGLETON extends SpellProjectile implements Spell
{

   public REPELLO_MUGGLETON (Ollivanders2 plugin, Player player, Spells name,
                             Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         StationarySpell.REPELLO_MUGGLETON stat = new StationarySpell.REPELLO_MUGGLETON(player, location, StationarySpells.REPELLO_MUGGLETON, 5, duration);
         stat.flair(10);
         p.addStationary(stat);
         kill();
      }
   }

}
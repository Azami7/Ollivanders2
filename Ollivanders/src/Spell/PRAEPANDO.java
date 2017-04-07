package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpells;

public class PRAEPANDO extends SpellProjectile implements Spell
{

   public PRAEPANDO (Ollivanders2 plugin, Player player, Spells name,
                     Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         StationarySpell.PRAEPANDO prae = new StationarySpell.PRAEPANDO(player, location, StationarySpells.PRAEPANDO, 1, duration, 5);
         prae.flair(10);
         p.addStationary(prae);
         kill();
      }
   }

}

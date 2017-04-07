package Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;

public class ALOHOMORA extends SpellProjectile implements Spell
{

   public ALOHOMORA (Ollivanders2 plugin, Player player, Spells name,
                     Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> inside = new ArrayList<StationarySpellObj>();
      for (StationarySpellObj spell : p.getStationary())
      {
         if (spell instanceof StationarySpell.COLLOPORTUS)
         {
            if (spell.isInside(location))
            {
               inside.add(spell);
               kill();
            }
         }
      }
      int subAmount = (int) ((usesModifier * 1200) / inside.size());
      for (StationarySpellObj spell : inside)
      {
         spell.age(subAmount);
         spell.flair(10);
      }
   }

}
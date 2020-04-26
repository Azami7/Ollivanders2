package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;

/**
 * Shortens the duration of shield spells.
 *
 * @author lownes
 * @author Azami7
 */
public final class SCUTO_CONTERAM extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public SCUTO_CONTERAM ()
   {
      super();

      spellType = O2SpellType.SCUTO_CONTERAM;
      text = "Scuto conteram will shorten the duration of a stationary spell.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public SCUTO_CONTERAM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.SCUTO_CONTERAM;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> inside = new ArrayList<>();
      for (StationarySpellObj spell : Ollivanders2API.getStationarySpells().getActiveStationarySpells())
      {
         if (spell.isInside(location))
         {
            inside.add(spell);
            kill();
         }
      }
      int subAmount = (int) ((usesModifier * 1200) / inside.size());
      for (StationarySpellObj spell : inside)
      {
         spell.duration -= subAmount;
         spell.flair(10);
      }
   }
}
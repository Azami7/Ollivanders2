package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;

/**
 * Lengthens the duration of shield spells.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FIANTO_DURI extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FIANTO_DURI ()
   {
      super();

      spellType = O2SpellType.FIANTO_DURI;

      flavorText = new ArrayList<String>() {{
         add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" - Filius Flitwick");
         add("");
      }};

      text = "Fianto Duri lengthens the duration of a stationary spell.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FIANTO_DURI (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FIANTO_DURI;
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
      int addedAmount = (int) ((usesModifier * 1200) / inside.size());
      for (StationarySpellObj spell : inside)
      {
         spell.duration += addedAmount;
         spell.flair(10);
      }
   }
}
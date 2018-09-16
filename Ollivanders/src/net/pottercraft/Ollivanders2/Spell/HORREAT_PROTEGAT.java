package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * Shrinks a stationarySpellObject's radius. Only the player who created the stationarySpellObject can change it's size.
 *
 * @author lownes
 * @author Azami7
 */
public final class HORREAT_PROTEGAT extends Charms
{
   public O2SpellType spellType = O2SpellType.HORREAT_PROTEGAT;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Spell-Reduction Charm");
   }};

   protected String text = "Horreat Protegat will shrink a stationary spell's radius. Only the creator of the stationary spell can affect it with this spell.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public HORREAT_PROTEGAT () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public HORREAT_PROTEGAT (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> inside = new ArrayList<>();
      for (StationarySpellObj spell : p.stationarySpells.getActiveStationarySpells())
      {
         if (spell.isInside(location) && spell.radius > (int) (10 / usesModifier))
         {
            inside.add(spell);
            kill();
         }
      }

      int limit = (int) (10 / usesModifier);
      if (limit < 1)
      {
         limit = 1;
      }
      for (StationarySpellObj spell : inside)
      {
         if (spell.radius > limit && spell.getCasterID().equals(player.getUniqueId()))
         {
            spell.radius--;
            spell.flair(10);
         }
      }
   }
}
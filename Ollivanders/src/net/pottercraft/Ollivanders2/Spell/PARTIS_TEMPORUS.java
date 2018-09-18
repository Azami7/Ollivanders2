package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

import java.util.ArrayList;

/**
 * Temporarily disables a stationary spell's effects if it is your spell.
 *
 * @author lownes
 * @author Azami7
 */
public final class PARTIS_TEMPORUS extends Charms
{
   public boolean move;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PARTIS_TEMPORUS ()
   {
      super();

      spellType = O2SpellType.PARTIS_TEMPORUS;

      flavorText = new ArrayList<String>() {{
         add("The Parting Charm");
      }};

      text = "Partis temporus, if cast at a stationary spell that you have cast, will cause that stationary spell's effects to stop for a short time.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PARTIS_TEMPORUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PARTIS_TEMPORUS;
      move = true;
   }

   @Override
   public void checkEffect ()
   {
      if (move)
      {
         move();
      }
      else
      {
         lifeTicks++;
      }
      for (StationarySpellObj spell : p.stationarySpells.getActiveStationarySpells())
      {
         if (spell.isInside(location) && spell.getCasterID().equals(player.getUniqueId()))
         {
            spell.active = false;
            spell.flair(10);
            move = false;
         }
      }
      if (lifeTicks > 160)
      {
         for (StationarySpellObj spell : p.stationarySpells.getActiveStationarySpells())
         {
            if (spell.isInside(location))
            {
               spell.active = true;
               spell.flair(10);
            }
         }
         kill = true;
      }
   }
}
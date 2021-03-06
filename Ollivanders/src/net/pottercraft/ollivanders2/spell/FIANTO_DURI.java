package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import org.jetbrains.annotations.NotNull;

/**
 * Lengthens the duration of shield spells.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class FIANTO_DURI extends O2Spell
{
   /**
    * Shield spells that can be targeted by this spell.
    */
   private ArrayList<O2StationarySpellType> shieldSpells = new ArrayList<O2StationarySpellType>()
   {{
      add(O2StationarySpellType.PROTEGO);
      add(O2StationarySpellType.PROTEGO_HORRIBILIS);
      add(O2StationarySpellType.PROTEGO_MAXIMA);
      add(O2StationarySpellType.PROTEGO_TOTALUM);
      add(O2StationarySpellType.MUFFLIATO);
      add(O2StationarySpellType.MOLLIARE);
      add(O2StationarySpellType.NULLUM_APPAREBIT);
      add(O2StationarySpellType.NULLUM_EVANESCUNT);
   }};

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FIANTO_DURI ()
   {
      super();
      spellType = O2SpellType.FIANTO_DURI;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" - Filius Flitwick");
         add("");
      }};

      text = "Fianto Duri lengthens the duration of a stationary spell.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FIANTO_DURI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FIANTO_DURI;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   /**
    * Look for any shield spells in the projectile location and increase their duration
    */
   @Override
   protected void doCheckEffect ()
   {
      List<StationarySpellObj> inside = new ArrayList<>();

      for (StationarySpellObj spell : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         // if the stationary spell type is not in the blacklist for this spell
         // was cast by the caster of this spell
         // and is inside the radius of this spell, then target it
         if (shieldSpells.contains(spell.getSpellType()) && spell.isInside(location) && spell.radius < (int) usesModifier)
         {
            inside.add(spell);

            break;
         }
      }

      // if we found a target stationary spells, increase their durations
      if (inside.size() > 0)
      {
         int addedAmount = (int) ((usesModifier * 1200) / inside.size());

         for (StationarySpellObj spell : inside)
         {
            spell.duration += addedAmount;
            spell.flair(10);
         }

         kill();
         return;
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}
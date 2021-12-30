package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.ShieldSpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
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
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public FIANTO_DURI(Ollivanders2 plugin)
   {
      super(plugin);
      spellType = O2SpellType.FIANTO_DURI;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>() {{
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
   protected void doCheckEffect()
   {
      List<O2StationarySpell> inside = new ArrayList<>();

      for (O2StationarySpell spell : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         // if the stationary spell type is not in the blacklist for this spell
         // was cast by the caster of this spell
         // and is inside the radius of this spell, then target it
         if ((spell instanceof ShieldSpell) && spell.isInside(location) && spell.radius < (int) usesModifier)
         {
            inside.add(spell);

            break;
         }
      }

      // if we found a target stationary spells, increase their durations
      if (inside.size() > 0)
      {
         int addedAmount = (int) ((usesModifier * 1200) / inside.size());

         for (O2StationarySpell spell : inside)
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
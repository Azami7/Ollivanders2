package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.ShieldSpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;

/**
 * Shortens the duration of shield spells. https://harrypotter.fandom.com/wiki/Shield_penetration_spell
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class SCUTO_CONTERAM extends O2Spell
{
   /**
    * The number of shield spells that can be targeted by this spell.
    */
   private int targets = 1;

   /**
    * The amount to reduce the duration of the shields.
    */
   private int percent = 1;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public SCUTO_CONTERAM ()
   {
      super();

      spellType = O2SpellType.SCUTO_CONTERAM;
      branch = O2MagicBranch.CHARMS;

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
      branch = O2MagicBranch.CHARMS;
      initSpell();

      percent = (int) usesModifier / 10;
      if (percent > 100)
      {
         percent = 100;
      }

      targets = (int) usesModifier / 20;
      if (targets < 1)
      {
         targets = 1;
      }
   }

   /**
    * Look for shield spells and shorten their duration
    */
   @Override
   protected void doCheckEffect()
   {
      for (StationarySpellObj stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location))
      {
         if (stationarySpell instanceof ShieldSpell)
         {
            stationarySpell.ageByPercent(percent);
            stationarySpell.flair(10);

            targets--;
         }

         if (targets < 1)
         {
            kill();
            return;
         }
      }

      if (hasHitTarget())
      {
         kill();
      }
   }
}
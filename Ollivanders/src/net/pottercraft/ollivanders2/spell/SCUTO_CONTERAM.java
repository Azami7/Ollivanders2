package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

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
   private int targetsRemaining = 1;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public SCUTO_CONTERAM(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.SCUTO_CONTERAM;
      branch = O2MagicBranch.CHARMS;

      text = "Scuto conteram will shorten the duration of some stationary spells.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public SCUTO_CONTERAM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.SCUTO_CONTERAM;
      branch = O2MagicBranch.CHARMS;
      initSpell();
   }

   /**
    * Initialize spell data
    */
   @Override
   void doInitSpell()
   {
      targetsRemaining = (int) usesModifier / 20;
      if (targetsRemaining < 1)
      {
         targetsRemaining = 1;
      }
   }

   /**
    * Look for shield spells and shorten their duration
    */
   @Override
   protected void doCheckEffect()
   {
      for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells(p).getStationarySpellsAtLocation(location))
      {
         if (stationarySpell.getSpellType().getLevel().ordinal() <= spellType.getLevel().ordinal())
            stationarySpell.kill();

         targetsRemaining = targetsRemaining - 1;
         if (targetsRemaining <= 0)
            break;
      }

      // kill the spell if the projectile has stopped or we have hit the max number of targets
      if (hasHitTarget() || targetsRemaining <= 0)
      {
         kill();
      }
   }
}
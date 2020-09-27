package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;

/**
 * Grows a Stationary Spell's radius. Only the player who created the Stationary Spell can change it's radius.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class CRESCERE_PROTEGAT extends O2Spell
{
   /**
    * Stationary spell types that cannot be targeted by this spell.
    */
   ArrayList<O2StationarySpellType> spellBlacklist = new ArrayList<O2StationarySpellType>()
   {{
      add(O2StationarySpellType.COLLOPORTUS);
      add(O2StationarySpellType.HORCRUX);
      add(O2StationarySpellType.HARMONIA_NECTERE_PASSUS);
      add(O2StationarySpellType.ALIQUAM_FLOO);
   }};

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CRESCERE_PROTEGAT ()
   {
      super();

      spellType = O2SpellType.CRESCERE_PROTEGAT;
      branch = O2MagicBranch.CHARMS;

      text = "Grows a stationary spell's radius. Only the player who created the Stationary Spell can change it's radius.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public CRESCERE_PROTEGAT (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.CRESCERE_PROTEGAT;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   /**
    * Look for stationary spells at the projectile's target location and increase its radius
    */
   @Override
   protected void doCheckEffect ()
   {
      StationarySpellObj inside = null;

      for (StationarySpellObj spell : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         // if the stationary spell type is not in the blacklist for this spell
         // was cast by the caster of this spell
         // and is inside the radius of this spell, then target it
         if (!spellBlacklist.contains(spell.getSpellType()) && spell.getCasterID().equals(player.getUniqueId())
               && spell.isInside(location) && spell.radius < (int) usesModifier)
         {
            inside = spell;

            break;
         }
      }

      // if we found a target stationary spell, increase its radius
      if (inside != null)
      {
         int limit = (int) usesModifier;

         if (inside.radius < limit && inside.getCasterID().equals(player.getUniqueId()))
         {
            inside.radius++;
            inside.flair(10);

            kill();
            return;
         }
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}
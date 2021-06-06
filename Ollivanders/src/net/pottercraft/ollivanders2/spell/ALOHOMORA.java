package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;

/**
 * The unlocking spell.
 *
 * @author cakenggt
 * @author Azami7
 * @version Ollivanders2
 */
public final class ALOHOMORA extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ALOHOMORA()
   {
      super();

      spellType = O2SpellType.ALOHOMORA;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("There are many ways to pass through locked doors in the magical world.  When you wish to enter or depart discreetly, however, the Unlocking Charm is your best friend.");
         add("The Unlocking Charm");
      }};

      text = "Unlocks blocks locked by Colloportus.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ALOHOMORA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.ALOHOMORA;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // world guard
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.INTERACT);
   }

   /**
    * Checks for colloportus stationary spells and ages them, if found
    */
   @Override
   protected void doCheckEffect ()
   {
      // check all the stationary spells in the location of the projectile for a Colloportus
      List<StationarySpellObj> inside = new ArrayList<>();
      for (StationarySpellObj spell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location))
      {
         if (spell instanceof COLLOPORTUS)
         {
            inside.add(spell);
         }
      }

      // remove the colloportus spells found
      if (inside.size() > 0)
      {
         for (StationarySpellObj spell : inside)
         {
            spell.kill();
            spell.flair(10);
         }

         kill();
      }

      // if the spell has hit a solid block, the projectile is stopped and wont go further so kill the spell
      if (hasHitTarget())
         kill();
   }
}
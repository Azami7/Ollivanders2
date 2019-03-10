package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * The unlocking spell.
 *
 * @version Ollivanders2
 * @author cakenggt
 * @author Azami7
 */
public final class ALOHOMORA extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ALOHOMORA ()
   {
      super();

      spellType = O2SpellType.ALOHOMORA;

      flavorText = new ArrayList<String>() {{
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
      setUsesModifier();

      worldGuardFlags.add(DefaultFlag.INTERACT);
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

      // age all the colloportus spells found
      if (inside.size() > 0)
      {
         int subAmount = (int) ((usesModifier * 1200) / inside.size());
         for (StationarySpellObj spell : inside)
         {
            spell.age(subAmount);
            spell.flair(10);
         }

         kill();
      }

      // if the spell has hit a solid block, the projectile is stopped and wont go further so kill the spell
      if (hasHitTarget())
      {
         kill();
      }
   }
}
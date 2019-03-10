package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * Grows a Stationary Spell's radius. Only the player who created the Stationary Spell can change it's radius.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class CRESCERE_PROTEGAT extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CRESCERE_PROTEGAT ()
   {
      super();

      spellType = O2SpellType.CRESCERE_PROTEGAT;
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
      setUsesModifier();
   }

   /**
    * Look for stationary spells at the projectile's target location and increase its radius
    */
   @Override
   protected void doCheckEffect ()
   {
      List<StationarySpellObj> inside = new ArrayList<>();

      for (StationarySpellObj spell : Ollivanders2API.getStationarySpells().getActiveStationarySpells())
      {
         if (spell.isInside(location) && spell.radius < (int) usesModifier)
         {
            inside.add(spell);
            kill();
         }
      }

      int limit = (int) usesModifier;
      for (StationarySpellObj spell : inside)
      {
         if (spell.radius < limit && spell.getCasterID().equals(player.getUniqueId()))
         {
            spell.radius++;
            spell.flair(10);
         }
      }
   }
}
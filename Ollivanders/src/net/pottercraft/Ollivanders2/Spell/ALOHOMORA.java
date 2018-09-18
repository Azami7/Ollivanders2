package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS;
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
   }

   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> inside = new ArrayList<>();
      for (StationarySpellObj spell : p.stationarySpells.getActiveStationarySpells())
      {
         if (spell instanceof COLLOPORTUS)
         {
            if (spell.isInside(location))
            {
               inside.add(spell);
               kill();
            }
         }
      }
      int subAmount = (int) ((usesModifier * 1200) / inside.size());
      for (StationarySpellObj spell : inside)
      {
         spell.age(subAmount);
         spell.flair(10);
      }
   }
}
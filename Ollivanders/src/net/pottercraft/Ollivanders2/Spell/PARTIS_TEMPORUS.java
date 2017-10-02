package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

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

      flavorText.add("The Parting Charm");

      text = "Partis temporus, if cast at a stationary spell that you have cast, will cause that stationary spell's effects to stop for a short time.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public PARTIS_TEMPORUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
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
      for (StationarySpellObj spell : p.getStationary())
      {
         if (spell.isInside(location) && spell.getPlayerUUID().equals(player.getUniqueId()))
         {
            spell.active = false;
            spell.flair(10);
            move = false;
         }
      }
      if (lifeTicks > 160)
      {
         for (StationarySpellObj spell : p.getStationary())
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
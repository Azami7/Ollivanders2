package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

/**
 * Create a pocket of extra-dimensional space.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class PRAEPANDO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PRAEPANDO ()
   {
      super();

      spellType = O2SpellType.PRAEPANDO;

      text = "Praepando is a space-extension spell which allows you to create a pocket of extra-dimensional space at a location. "
            + "Spells can travel from the extra-dimensional pocket through to the real-world, but cannot go the other way around.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PRAEPANDO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PRAEPANDO;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.PRAEPANDO prae = new net.pottercraft.Ollivanders2.StationarySpell.PRAEPANDO(p, player.getUniqueId(), location, O2StationarySpellType.PRAEPANDO,
               1, duration, 5);
         prae.flair(10);
         Ollivanders2API.getStationarySpells().addStationarySpell(prae);
         kill();
      }
   }
}

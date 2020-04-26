package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;

/**
 * Makes an anti-disapparition spell. Players can't apparate out of it.
 *
 * @author lownes
 */
public final class NULLUM_EVANESCUNT extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public NULLUM_EVANESCUNT ()
   {
      super();

      spellType = O2SpellType.NULLUM_EVANESCUNT;
      text = "Nullum evanescunt creates a stationary spell which will not allow disapparition out of it.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public NULLUM_EVANESCUNT (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.NULLUM_EVANESCUNT;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();
      Material targetBlockType = getBlock().getType();
      if (targetBlockType != Material.AIR && targetBlockType != Material.FIRE && targetBlockType != Material.WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT nullum = new net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT(p, player.getUniqueId(), location, O2StationarySpellType.NULLUM_EVANESCUNT, 5, duration);
         nullum.flair(10);
         Ollivanders2API.getStationarySpells().addStationarySpell(nullum);
         kill();
      }
   }
}
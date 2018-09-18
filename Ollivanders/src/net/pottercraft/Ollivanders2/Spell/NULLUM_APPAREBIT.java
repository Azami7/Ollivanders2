package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

/**
 * Creates an Anti-apparition spell object.
 *
 * @author lownes
 * @author Azami7
 */
public final class NULLUM_APPAREBIT extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public NULLUM_APPAREBIT ()
   {
      super();

      spellType = O2SpellType.NULLUM_APPAREBIT;
      text = "Nullum apparebit creates a stationary spell which will not allow apparition into it.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public NULLUM_APPAREBIT (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.NULLUM_APPAREBIT;
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.NULLUM_APPAREBIT nullum = new net.pottercraft.Ollivanders2.StationarySpell.NULLUM_APPAREBIT(p, player, location, StationarySpells.NULLUM_APPAREBIT, 5, duration);
         nullum.flair(10);
         p.stationarySpells.addStationarySpell(nullum);
         kill();
      }
   }
}
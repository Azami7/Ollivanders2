package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

import java.util.ArrayList;

/**
 * Creates a muffliato stationary spell object. Only players within that
 * object can hear other players within it. Time duration depends on spell's
 * level.
 *
 * @author lownes
 * @author Azami7
 */
public final class MUFFLIATO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MUFFLIATO ()
   {
      super();

      spellType = O2SpellType.MUFFLIATO;

      flavorText = new ArrayList<String>() {{
         add(" [...] perhaps most useful of all, Muffliato, a spell that filled the ears of anyone nearby with an unidentifiable buzzing, so that lengthy conversations could be held in class without being overheard.");
      }};

      text = "Muffliato creates a stationary spell which only allows the people inside to hear anything spoken inside the effect.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MUFFLIATO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.MUFFLIATO;
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (super.getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) usesModifier * 1200;
         net.pottercraft.Ollivanders2.StationarySpell.MUFFLIATO muffliato = new net.pottercraft.Ollivanders2.StationarySpell.MUFFLIATO(p, player, location,
               StationarySpells.MUFFLIATO, 5, duration);
         muffliato.flair(20);
         p.stationarySpells.addStationarySpell(muffliato);
         kill();
      }
   }
}
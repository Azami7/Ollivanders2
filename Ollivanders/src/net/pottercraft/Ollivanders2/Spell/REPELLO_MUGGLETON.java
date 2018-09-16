package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

import java.util.ArrayList;

/**
 * Adds a repello muggleton stationary spell object.
 *
 * @author lownes
 * @author Azami7
 */
public final class REPELLO_MUGGLETON extends Charms
{
   public O2SpellType spellType = O2SpellType.REPELLO_MUGGLETON;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("Muggle-Repelling Charms on every inch of it. Every time Muggles have got anywhere near here all year, they've suddenly remembered urgent appointments and had to dash away again.");
   }};

   protected String text = "Repello Muggleton will hide any blocks and players in it's radius from those outside of it.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REPELLO_MUGGLETON () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REPELLO_MUGGLETON (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.REPELLO_MUGGLETON stat = new net.pottercraft.Ollivanders2.StationarySpell.REPELLO_MUGGLETON(p, player, location,
               StationarySpells.REPELLO_MUGGLETON, 5, duration);
         stat.flair(10);
         p.stationarySpells.addStationarySpell(stat);
         kill();
      }
   }
}
package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;
import org.bukkit.Material;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

import java.util.ArrayList;

/**
 * Makes a spell projectile that creates a shield that hurts any entities within 0.5 meters of the spell wall.
 *
 * @author lownes
 * @author Azami7
 */
public final class PROTEGO_MAXIMA extends Charms
{
   public O2SpellType spellType = O2SpellType.PROTEGO_MAXIMA;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" -Filius Flitwick");
      add("A Stronger Shield Charm");
   }};

   protected String text = "Protego maxima is a stationary spell which will hurt any entities close to it's boundary.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_MAXIMA () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO_MAXIMA (Ollivanders2 plugin, Player player, Double rightWand)
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
         double damage = usesModifier / 10;
         net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_MAXIMA max =
               new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_MAXIMA(p, player, location, StationarySpells.PROTEGO_MAXIMA, 5, duration, damage);
         max.flair(10);
         p.stationarySpells.addStationarySpell(max);
         kill();
      }
   }
}
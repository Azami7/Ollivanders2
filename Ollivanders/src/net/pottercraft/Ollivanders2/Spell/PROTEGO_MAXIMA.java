package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Makes a spell projectile that creates a shield that hurts any entities within 0.5 meters of the spell wall.
 *
 * @author lownes
 * @author Azami7
 */
public final class PROTEGO_MAXIMA extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_MAXIMA ()
   {
      super();

      spellType = O2SpellType.PROTEGO_MAXIMA;

      flavorText = new ArrayList<String>() {{
         add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" -Filius Flitwick");
         add("A Stronger Shield Charm");
      }};

      text = "Protego maxima is a stationary spell which will hurt any entities close to it's boundary.";
   }

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

      spellType = O2SpellType.PROTEGO_MAXIMA;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      Material targetBlockType = getBlock().getType();
      if (targetBlockType != Material.AIR && targetBlockType != Material.FIRE && targetBlockType != Material.WATER)
      {
         int duration = (int) (usesModifier * 1200);
         double damage = usesModifier / 10;
         net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_MAXIMA max =
               new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_MAXIMA(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO_MAXIMA, 5, duration, damage);
         max.flair(10);
         Ollivanders2API.getStationarySpells().addStationarySpell(max);
         kill();
      }
   }
}
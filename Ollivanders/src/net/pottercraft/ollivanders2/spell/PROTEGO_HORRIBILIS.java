package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Protego horribilis is the incantation to a protective spell.
 *
 * @author cakenggt
 * @author Azami7
 */
public final class PROTEGO_HORRIBILIS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_HORRIBILIS ()
   {
      super();

      spellType = O2SpellType.PROTEGO_HORRIBILIS;

      flavorText = new ArrayList<String>() {{
         add(" [...] although he could barely see out of it, he pointed his wand through the smashed window and started muttering incantations of great complexity. Harry heard a weird rushing noise, as though Flitwick had unleashed the power of the wind into the grounds.");
      }};

      text = "Protego horribilis is a stationary spell which will destroy any spells crossing it's barrier.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO_HORRIBILIS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PROTEGO_HORRIBILIS;
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
         net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS total =
               new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO_HORRIBILIS, 5, duration);
         total.flair(10);
         Ollivanders2API.getStationarySpells().addStationarySpell(total);
         kill();
      }
   }
}
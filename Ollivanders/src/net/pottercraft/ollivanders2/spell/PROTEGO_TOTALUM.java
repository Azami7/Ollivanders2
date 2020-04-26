package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Creates a PROTEGO_TOTALUM Stationary Spell Object
 *
 * @author lownes
 * @author Azami7
 */
public final class PROTEGO_TOTALUM extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_TOTALUM ()
   {
      super();

      spellType = O2SpellType.PROTEGO_TOTALUM;

      flavorText = new ArrayList<String>() {{
         add("Raising her wand, she began to walk in a wide circle around Harry and Ron, murmuring incantations as she went. Harry saw little disturbances in the surrounding air: it was as if Hermione had cast a heat haze across their clearing.");
      }};

      text = "Protego totalum is a stationary spell which will prevent any entities from crossing it's boundary.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO_TOTALUM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PROTEGO_TOTALUM;
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
         net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM total =
               new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO_TOTALUM, 5, duration);
         total.flair(10);
         Ollivanders2API.getStationarySpells().addStationarySpell(total);
         kill();
      }
   }
}
package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Locks blocks in to place.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class COLLOPORTUS extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLLOPORTUS()
   {
      super();

      spellType = O2SpellType.COLLOPORTUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("The Locking Spell.");
      }};

      text = "Locks blocks in to place. This spell does not age and can only be removed with the Unlocking Spell, Alohomora.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLLOPORTUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.COLLOPORTUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
   }

   @Override
   protected StationarySpellObj createStationarySpell ()
   {
      return new net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS(p, player.getUniqueId(), location, O2StationarySpellType.COLLOPORTUS, radius, duration);
   }
}
package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * The Revealing Charm. Causes any stationary spell objects to flair with an intensity equal to your level.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class APARECIUM extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public APARECIUM()
   {
      super();

      spellType = O2SpellType.APARECIUM;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("The Revealing Charm will reveal invisible ink and messages hidden by magical means. Simply tap a book or parchment with your wand and any hidden message will be revealed. This spell is more than sufficient to overcome the basic concealing charms and so is a favourite of parents and teachers alike.");
         add("The Revealing Charm");
      }};

      text = "Causes any area spells to reveal their borders.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public APARECIUM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.APARECIUM;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   /**
    * If any stationary spells are at the location of the spell projectile, make them flair.
    */
   @Override
   protected void doCheckEffect ()
   {
      List<StationarySpellObj> stationaries = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);

      if (stationaries.size() > 0)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("Found " + stationaries.size() + " stationary spells");
         }

         stationaries.get(0).flair(10);

         kill();
         return;
      }

      // if the spell has hit a solid block, the projectile is stopped and wont go further so kill the spell
      if (hasHitTarget())
         kill();
   }
}
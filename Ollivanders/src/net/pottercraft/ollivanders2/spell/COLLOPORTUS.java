package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.jetbrains.annotations.NotNull;

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
    *
    * @param plugin the Ollivanders2 plugin
    */
   public COLLOPORTUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.COLLOPORTUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Locking Spell.");
      }};

      text = "Locks blocks in to place. This spell does not age and can only be removed with the Unlocking Spell, Alohomora.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLLOPORTUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.COLLOPORTUS;
      branch = O2MagicBranch.CHARMS;

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.BUILD);
      }

      initSpell();

      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
   }

   @Override
   @NotNull
   protected O2StationarySpell createStationarySpell()
   {
      return new net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS(p, player.getUniqueId(), location, O2StationarySpellType.COLLOPORTUS, radius, duration);
   }
}
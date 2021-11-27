package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Creates a muffliato stationary spell object. Only players within that
 * object can hear other players within it. Time duration depends on spell's
 * level.
 *
 * @author lownes
 * @author Azami7
 */
public final class MUFFLIATO extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public MUFFLIATO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.MUFFLIATO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add(" [...] perhaps most useful of all, Muffliato, a spell that filled the ears of anyone nearby with an unidentifiable buzzing, so that lengthy conversations could be held in class without being overheard.");
      }};

      text = "Muffliato creates a stationary spell which only allows the people inside to hear anything spoken inside the effect.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MUFFLIATO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.MUFFLIATO;
      branch = O2MagicBranch.CHARMS;

      baseDurationInSeconds = 30;
      durationModifierInSeconds = 10;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;

      initSpell();
   }

   @Override
   protected O2StationarySpell createStationarySpell()
   {
      return new net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO(p, player.getUniqueId(), location, O2StationarySpellType.MUFFLIATO, radius, duration);
   }
}
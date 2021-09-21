package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Creates an Anti-apparition spell object.
 *
 * @author lownes
 * @author Azami7
 */
public final class NULLUM_APPAREBIT extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public NULLUM_APPAREBIT(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.NULLUM_APPAREBIT;
      branch = O2MagicBranch.CHARMS;

      text = "Nullum apparebit creates a stationary spell which will not allow apparition into it.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.NULLUM_APPAREBIT;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      baseDurationInSeconds = 300;
      durationModifierInSeconds = 15;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;
   }

   @Override
   protected O2StationarySpell createStationarySpell()
   {
      return new net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT(p, player.getUniqueId(), location, O2StationarySpellType.NULLUM_APPAREBIT, radius, duration);
   }
}
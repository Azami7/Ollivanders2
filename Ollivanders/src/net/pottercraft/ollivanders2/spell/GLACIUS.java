package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Glacius will cause a great cold to descend in a radius from it's impact point which freezes blocks. The radius and
 * duration of the freeze depend on your experience.
 *
 * @see GlaciusSuper
 * @author Azami7
 */
public final class GLACIUS extends GlaciusSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public GLACIUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.GLACIUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>() {{
         add("A charm that conjures a blast of freezing cold air from the end of the wand.");
         add("The Freezing Charm");
      }};

      text = "Turns fire in to air, water in to ice, ice to packed ice, and lava in to obsidian.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public GLACIUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.GLACIUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // normal duration
      durationModifier = 1.0;
      // normal radius
      radiusModifier = 1.0;
   }
}